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

    private final EventLoopGroup group;
    private final Bootstrap bootstrap;
    private final Channel channel;
    private final List<InetSocketAddress> availableServers;
    private final NgClientHandler ngClientHandler;
    private ChannelStatsManager statsManager;
    private List<InetSocketAddress> servers;
    private List<NgResultListener> resultListeners;
    private List<NgCommandListener> commandListeners;
    private ServerLocator serverLocator;

    public NgClient() throws InterruptedException {
        availableServers = new CopyOnWriteArrayList<InetSocketAddress>();
        group = new NioEventLoopGroup();
        ngClientHandler = new NgClientHandler(getResultListeners());
        bootstrap = new Bootstrap();
        channel = createChannel();
        serverLocator = new ConsistentHashServerLocator(); // default
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
     * and setting it in property {@link NgClient#setResultListeners(java.util.List)}
     *
     * @param command Command to be sent
     * @param key hash key used to stick the sender to a media proxy node, so that further commands are going to be
     *            sent to the same instance
     * @throws Exception
     */
    public void send(NgCommand command, String key) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("Sending message: "+ command.toString() + " - key: " + key);
        }
        final InetSocketAddress server = getServerLocator().selectServer(key, getAvailableServers());
        sendDirect(command, server);
    }

    /**
     * Send a command to media proxy <br><br>
     *
     * Media proxy responses can be received by implementing {@link com.upptalk.jinglertpengine.ng.NgResultListener}
     * and setting it in property {@link NgClient#setResultListeners(java.util.List)}
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

    /**
     * Set the result listeners
     *
     * @param resultListeners
     */
    public void setResultListeners(List<NgResultListener> resultListeners) {
        this.resultListeners = resultListeners;
        ngClientHandler.setListeners(resultListeners);
    }

    public List<NgCommandListener> getCommandListeners() {
        return commandListeners;
    }

    /**
     * Set the command listeners
     *
     * @param commandListeners
     */
    public void setCommandListeners(List<NgCommandListener> commandListeners) {
        this.commandListeners = commandListeners;
    }

    public ChannelStatsManager getStatsManager() {
        return statsManager;
    }

    public void setStatsManager(ChannelStatsManager statsManager) {
        this.statsManager = statsManager;
    }

    public ServerLocator getServerLocator() {
        return serverLocator;
    }

    public void setServerLocator(ServerLocator serverLocator) {
        this.serverLocator = serverLocator;
    }
}
