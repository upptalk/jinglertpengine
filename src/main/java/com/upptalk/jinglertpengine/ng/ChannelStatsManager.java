package com.upptalk.jinglertpengine.ng;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.upptalk.jinglertpengine.ng.protocol.NgCommand;
import com.upptalk.jinglertpengine.ng.protocol.NgCommandType;
import com.upptalk.jinglertpengine.ng.protocol.NgResult;
import com.upptalk.jinglertpengine.ng.protocol.NgResultType;
import com.upptalk.jinglertpengine.util.RandomString;
import org.apache.log4j.Logger;
import org.jivesoftware.smack.util.collections.DefaultMapEntry;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Channel stats manager
 *
 * @author bhlangonijr
 *         Date: 4/10/14
 *         Time: 10:51 AM
 */
public class ChannelStatsManager implements NgResultListener, NgCommandListener {

    private static final Logger log = Logger.getLogger(ChannelStatsManager.class);
    public static final long DEFAULT_CHANNEL_TIMEOUT_VALUE = 20000;
    public static final long DEFAULT_CHANNEL_AVAILABLE_TASK_DELAY_VALUE = 60000;
    private final Map<InetSocketAddress, ChannelStats> channelStats;
    private final Map<String, Map.Entry<InetSocketAddress, NgCommand>> pendingMessages;
    private NgClient ngClient;
    private long channelTimeout = DEFAULT_CHANNEL_TIMEOUT_VALUE;
    private final long channelAvailableTaskDelay;
    private final ScheduledExecutorService service;


    public ChannelStatsManager(long channelAvailableTaskDelay) {
        this.channelAvailableTaskDelay = channelAvailableTaskDelay;
        channelStats = new ConcurrentHashMap<InetSocketAddress, ChannelStats>();
        pendingMessages = new ConcurrentLinkedHashMap.Builder().maximumWeightedCapacity(10000).build();
        service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(new ChannelKeepAliveTask(), 0,
                getChannelAvailableTaskDelay(), TimeUnit.MILLISECONDS);
    }

    public ChannelStatsManager() {
        this(DEFAULT_CHANNEL_AVAILABLE_TASK_DELAY_VALUE);
    }

    // sends a ping to all servers to check further availability
    private class ChannelKeepAliveTask implements Runnable {

        @Override
        public void run() {
            Assert.notNull(getNgClient());
            // check for timed out channels
            if (log.isDebugEnabled()) {
                log.debug("Running channel keep-alive task... ");
            }

            for (InetSocketAddress server: getNgClient().getServers()) {
                if (getChannelStats(server).getSentCommands() < 1) {
                    continue;
                }
                if (log.isDebugEnabled()) {
                    log.debug("Checking if channel is live: " + server);
                }
                if (!isChannelAlive(server)) {
                    if (log.isDebugEnabled()) {
                        log.debug("Channel is not live:  " + server);
                    }
                    if (getNgClient().getAvailableServers().contains(server)) {
                        //consistent hashing can only deal with appending and removal from the end of a list
                        int n = getNgClient().getAvailableServers().indexOf(server);
                        getNgClient().getAvailableServers().set(n,
                                getNgClient().getAvailableServers().get(getNgClient().getAvailableServers().size() - 1));
                        getNgClient().getAvailableServers().remove(getNgClient().getAvailableServers().size() - 1);
                        log.warn("Channel ["+server.toString()+"] " +
                                "timed out on PING. Removing from list of available servers...");
                    } else {
                        if (log.isDebugEnabled()) {
                            log.debug("Channel is already removed from available servers:  " + server);
                        }
                    }
                } else if (!getNgClient().getAvailableServers().contains(server)) {
                    getNgClient().getAvailableServers().add(server);
                    log.warn("Channel ["+server.toString()+"] " +
                            "responded to PING. Adding to list of available servers...");
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("Channel is available:  " + server);
                    }
                }
            }

            // send pings for the next round check
            for (InetSocketAddress server: getNgClient().getServers()) {
                final NgCommand ping = NgCommand.builder()
                        .setCookie(RandomString.getCookie())
                        .setNgCommandType(NgCommandType.ping)
                        .build();
                try {
                    getNgClient().sendDirect(ping, server);
                } catch (Exception e) {
                    log.error("Failed sending PING command to server " + server, e);
                }
            }

        }
    }


    @Override
    public void receive(NgResult result) {

        final Map.Entry<InetSocketAddress, NgCommand> entry = pendingMessages.remove(result.getCookie());

        if (entry != null) {
            final ChannelStats stats = getChannelStats(entry.getKey());
            stats.addReceivedCommands();
            if (log.isDebugEnabled()) {
                log.debug("Updating stats: " + result.getCookie() + " / " + result.getNgResultType());
            }
            if (result.getNgResultType().equals(NgResultType.pong)) {
                stats.setLastPongTimestamp(System.currentTimeMillis());
            }
        } else {
            log.warn("Found no pending message with cookie [" + result.getCookie()  + "]" );
        }

    }

    @Override
    public void sent(NgCommand command, InetSocketAddress server) {

        final ChannelStats stats = getChannelStats(server);
        stats.addSentCommands();
        if (command.getNgCommandType().equals(NgCommandType.ping)) {
            stats.setLastPingTimestamp(System.currentTimeMillis());
        }

        pendingMessages.put(command.getCookie(),
                new DefaultMapEntry<InetSocketAddress, NgCommand>(server, command));

    }

    /**
     * Retrive live channel stats
     *
     * @param server
     * @return live stats of the channel
     */
    public ChannelStats getChannelStats(InetSocketAddress server) {

        ChannelStats stats = channelStats.get(server);
        if (stats == null) {
            stats = new ChannelStats();
            channelStats.put(server, stats);
        }

        return stats;
    }


    public long getChannelTimeout() {
        return channelTimeout;
    }

    public void setChannelTimeout(long channelTimeout) {
        this.channelTimeout = channelTimeout;
    }

    public long getChannelAvailableTaskDelay() {
        return channelAvailableTaskDelay;
    }

    public NgClient getNgClient() {
        return ngClient;
    }

    @Required
    public void setNgClient(NgClient ngClient) {
        this.ngClient = ngClient;
    }

    /**
     * Check whether the given server is replying to ping messages
     *
     * @param server host
     * @return true if the server is returning to ping messages within the timeout
     */
    public boolean isChannelAlive(InetSocketAddress server) {
        if (log.isDebugEnabled()) {
            log.debug("[" +server+ "] Now: " + System.currentTimeMillis() + " - lastPong: " +
                    getChannelStats(server).getLastPongTimestamp());
        }
        return (System.currentTimeMillis() -
                getChannelStats(server).getLastPongTimestamp()) < getChannelTimeout();
    }


    public void close() {
        service.shutdownNow();
    }
}
