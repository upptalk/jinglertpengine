package com.upptalk.jinglertpengine.ng;

import com.upptalk.jinglertpengine.ng.hash.ConsistentHashServerLocator;
import com.upptalk.jinglertpengine.ng.hash.ServerLocator;
import com.upptalk.jinglertpengine.ng.protocol.NgCommand;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;
import org.apache.log4j.Logger;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Media Proxy NG client implementation
 *
 * @see <a href="https://github.com/sipwise/rtpengine#the-ng-control-protocol">Ng Control Protocol</a>
 *
 * @author bhlangonijr
 *         Date: 4/7/14
 *         Time: 5:50 PM
 */
public class NgClient {

    private static final Logger log = Logger.getLogger(NgClient.class);
    public static final long DEFAULT_CHANNEL_TIMEOUT_VALUE = 20000;
    public static final long DEFAULT_CHANNEL_AVAILABLE_TASK_DELAY_VALUE = 60000;
    public static final int DEFAULT_TIMEOUT_THREAD_POOL_VALUE = 5;

    private final EventLoopGroup group;
    private final Bootstrap bootstrap;
    private final Channel channel;
    private final List<InetSocketAddress> availableServers;
    private final NgClientHandler ngClientHandler;
    private final List<NgResultListener> resultListeners;
    private final List<NgCommandListener> commandListeners;
    private final ChannelStatsManager statsManager;
    private List<InetSocketAddress> servers;
    private ServerLocator serverLocator;

    private final long channelTimeout;
    private final long channelAvailableTaskDelay;
    private final int timeoutTaskThreadPoolSize;

    /**
     * Creates a new NgClient object
     *
     * @param channelAvailableTaskDelay The delay for running the internal keep-alive task for checking
     *                                  available servers
     * @param channelTimeout Timeout in milliseconds for channels not responding to commands
     * @param timeoutTaskThreadPoolSize Number of threads running timeout checks for commands
     * @throws InterruptedException
     */
    public NgClient(long channelAvailableTaskDelay,
                    long channelTimeout, int timeoutTaskThreadPoolSize) throws InterruptedException {
        this.channelTimeout = channelTimeout;
        this.channelAvailableTaskDelay = channelAvailableTaskDelay;
        this.timeoutTaskThreadPoolSize = timeoutTaskThreadPoolSize;
        availableServers = new CopyOnWriteArrayList<InetSocketAddress>();
        group = new NioEventLoopGroup();
        ngClientHandler = new NgClientHandler(getResultListeners());
        bootstrap = new Bootstrap();
        channel = createChannel();
        serverLocator = new ConsistentHashServerLocator(); // default
        resultListeners = new CopyOnWriteArrayList<NgResultListener>();
        commandListeners = new CopyOnWriteArrayList<NgCommandListener>();
        statsManager = new ChannelStatsManager(this, channelAvailableTaskDelay,
                channelTimeout, timeoutTaskThreadPoolSize);
        ngClientHandler.setListeners(resultListeners);
    }

    /**
     * Creates a new NgClient Object
     * @throws InterruptedException
     */
    public NgClient() throws InterruptedException {
        this(DEFAULT_CHANNEL_AVAILABLE_TASK_DELAY_VALUE, DEFAULT_CHANNEL_TIMEOUT_VALUE,
                DEFAULT_TIMEOUT_THREAD_POOL_VALUE);
    }

    private Channel createChannel() throws InterruptedException {
        log.debug("Creating channel...");
        bootstrap.group(group)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
                .option(ChannelOption.SO_REUSEADDR, true)
                .handler(ngClientHandler);

        Channel channel = bootstrap.bind(0).sync().channel();
        return channel;
    }

    /**
     * Send a command to media proxy <br><br>
     *
     * Media proxy responses can be received by implementing {@link com.upptalk.jinglertpengine.ng.NgResultListener}
     * and setting it in property {@link com.upptalk.jinglertpengine.ng.NgClient#getResultListeners()}
     *
     * @param command Command to be sent
     * @param key hash key used to stick the sender to a media proxy node, so that further commands are going to be
     *            sent to the same instance
     * @throws Exception
     */
    public void send(NgCommand command, String key) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("Sending message: "+ command.toBencode() + " - key: " + key);
        }
        final InetSocketAddress server = getServerLocator().selectServer(key, getAvailableServers());
        sendDirect(command, server);
    }

    /**
     * Send a command to media proxy <br><br>
     *
     * Media proxy responses can be received by implementing {@link com.upptalk.jinglertpengine.ng.NgResultListener}
     * and adding it into the list {@link com.upptalk.jinglertpengine.ng.NgClient#getResultListeners()}
     *
     * @param command Command to be sent
     * @param server Address of media proxy server
     * @throws Exception
     */
    public void sendDirect(NgCommand command, InetSocketAddress server) throws Exception {
        channel.writeAndFlush(new DatagramPacket(
                Unpooled.copiedBuffer(command.toBencode(), CharsetUtil.UTF_8),
                server)).sync();
        if (getCommandListeners() != null) {
            for (NgCommandListener listener: getCommandListeners()) {
                listener.sent(command, server);
            }
        }
    }

    public void close() throws InterruptedException {
        try {
            statsManager.close();
            channel.close();
            if (!channel.closeFuture().await(5000)) {
                log.error("Request timed out.");
            }
        } finally {
            group.shutdownGracefully();
        }
    }

    public List<InetSocketAddress> getServers() {
        return servers;
    }

    public void setServers(List<InetSocketAddress> servers) {
        this.servers = servers;
        getAvailableServers().clear();
        getAvailableServers().addAll(servers);
    }

    public void setServers(InetSocketAddress ...servers) {
        List<InetSocketAddress> addresses = new ArrayList<InetSocketAddress>();
        for (InetSocketAddress as: servers) {
            addresses.add(as);
        }
        setServers(addresses);
    }

    public List<InetSocketAddress> getAvailableServers() {
        return availableServers;
    }

    public List<NgResultListener> getResultListeners() {
        return resultListeners;
    }

    public List<NgCommandListener> getCommandListeners() {
        return commandListeners;
    }

    public ChannelStatsManager getStatsManager() {
        return statsManager;
    }

    public ServerLocator getServerLocator() {
        return serverLocator;
    }

    public void setServerLocator(ServerLocator serverLocator) {
        this.serverLocator = serverLocator;
    }


}
