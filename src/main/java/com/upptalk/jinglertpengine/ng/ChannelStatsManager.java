package com.upptalk.jinglertpengine.ng;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.upptalk.jinglertpengine.ng.protocol.NgCommand;
import com.upptalk.jinglertpengine.ng.protocol.NgCommandType;
import com.upptalk.jinglertpengine.ng.protocol.NgResult;
import com.upptalk.jinglertpengine.ng.protocol.NgResultType;
import com.upptalk.jinglertpengine.util.NamingThreadFactory;
import com.upptalk.jinglertpengine.util.RandomString;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.*;

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
    public static final int DEFAULT_TIMEOUT_THREAD_POOL_VALUE = 5;
    private final Map<InetSocketAddress, ChannelStats> channelStats;
    private final Map<String, Entry> pendingMessages;
    private final Map<String, Future> timeoutTasks;
    private NgClient ngClient;
    private long channelTimeout = DEFAULT_CHANNEL_TIMEOUT_VALUE;
    private final long channelAvailableTaskDelay;
    private final int timeoutTaskThreadPoolSize;
    private final ScheduledExecutorService service;


    public ChannelStatsManager(long channelAvailableTaskDelay, int timeoutTaskThreadPoolSize) {
        this.channelAvailableTaskDelay = channelAvailableTaskDelay;
        this.timeoutTaskThreadPoolSize = timeoutTaskThreadPoolSize;
        channelStats = new ConcurrentHashMap<InetSocketAddress, ChannelStats>();
        pendingMessages = new ConcurrentLinkedHashMap.Builder().maximumWeightedCapacity(10000).build();
        timeoutTasks = new ConcurrentLinkedHashMap.Builder().maximumWeightedCapacity(10000).build();
        service = Executors.newScheduledThreadPool(timeoutTaskThreadPoolSize,
                new NamingThreadFactory("TimeoutTask"));
        service.scheduleAtFixedRate(new ChannelKeepAliveTask(), 0,
                getChannelAvailableTaskDelay(), TimeUnit.MILLISECONDS);
    }

    public ChannelStatsManager() {
        this(DEFAULT_CHANNEL_AVAILABLE_TASK_DELAY_VALUE, DEFAULT_TIMEOUT_THREAD_POOL_VALUE);
    }

    // controls timed out commands
    private class CommandTimeout implements Runnable {

        private final Entry entry;

        private CommandTimeout(Entry entry) {
            this.entry = entry;
        }

        @Override
        public void run() {
            Assert.notNull(getNgClient());
            for (NgResultListener listener: ngClient.getResultListeners()) {
                try {
                    if (log.isDebugEnabled()) {
                        log.debug("Timed out command: " + entry.getCommand());
                    }
                    final NgResult timeout = NgResult.builder().
                            setCookie(entry.getCommand().getCookie()).
                            setNgResultType(NgResultType.timeout).
                            build();
                    listener.receive(timeout); // send a timeout result
                } catch (Exception e) {
                    log.error("Error in timeout task runner", e);
                }
            }
        }
    }

    // sends a ping to all servers to check further availability
    private class ChannelKeepAliveTask implements Runnable {

        @Override
        public void run() {
            Assert.notNull(getNgClient());
            // check for timed out channels
            if (log.isDebugEnabled()) {
                log.debug("Running channel keep-alive task: ");
                log.debug("Pending messages: " + pendingMessages.size());
                log.debug("Timeout tasks: " + pendingMessages.size());
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

        final Entry entry = pendingMessages.remove(result.getCookie());
        final Future future = timeoutTasks.remove(result.getCookie());

        if (future != null) {
            try {
                if (!future.isCancelled() && !future.isDone()) {
                    future.cancel(true);
                    if (log.isDebugEnabled()) {
                        log.debug("Cancelling time out task: " + result.getCookie());
                    }
                }
            } catch (Exception e) {

            }
        }

        if (entry != null) {
            if (result.getNgResultType().equals(NgResultType.timeout)) {
                checkChannel(entry.getServer());
            } else {
                final ChannelStats stats = getChannelStats(entry.getServer());
                stats.addReceivedCommands();
                if (log.isDebugEnabled()) {
                    log.debug("Updating stats: " + result.getCookie() + " / " + result.getNgResultType());
                }
                if (result.getNgResultType().equals(NgResultType.pong)) {
                    stats.setLastPongTimestamp(System.currentTimeMillis());
                    checkChannel(entry.getServer());
                }
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
        final Entry entry = new Entry(server, command);
        pendingMessages.put(command.getCookie(), entry);
        ScheduledFuture future =
                service.schedule(new CommandTimeout(entry), getChannelTimeout(), TimeUnit.MILLISECONDS);

        timeoutTasks.put(command.getCookie(), future);
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

    /* command entry
     */
    private static class Entry  {
        private final long timestamp;
        private final InetSocketAddress server;
        private final NgCommand command;

        public Entry(InetSocketAddress key, NgCommand value) {
            this.timestamp = System.currentTimeMillis();
            this.server = key;
            this.command = value;
        }

        public InetSocketAddress getServer() {
            return server;
        }

        public NgCommand getCommand() {
            return command;
        }

        public long getTimestamp() {
            return timestamp;
        }

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

    public int getTimeoutTaskThreadPoolSize() {
        return timeoutTaskThreadPoolSize;
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

    private void checkChannel(final InetSocketAddress server) {

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


    public void close() {
        service.shutdownNow();
    }
}
