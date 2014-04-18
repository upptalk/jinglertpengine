package com.upptalk.jinglertpengine.ng;

/**
 * @author bhlangonijr
 *         Date: 4/9/14
 *         Time: 11:30 PM
 */

import com.upptalk.jinglertpengine.ng.protocol.NgResult;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * Handles messages received from media proxy
 */
class NgClientHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private static final Logger log = Logger.getLogger(NgClientHandler.class);
    private List<NgResultListener> listeners;

    public NgClientHandler(List<NgResultListener> listeners) {
        this.listeners = listeners;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
        final String response = msg.content().toString(CharsetUtil.UTF_8);
        if (log.isDebugEnabled()) {
            log.debug("Message received1: "+response);
        }
        try {
            final NgResult result = NgResult.fromBencode(new ByteArrayInputStream(response.getBytes()));
            if (log.isDebugEnabled()) {
                log.debug("Message received2: "+result.toString());
            }
            if (getListeners() != null) {
                for (NgResultListener listener: getListeners()) {
                    listener.receive(result);
                }
            } else {
                log.warn("Found not listeners to handle incoming messages from media proxy server");
            }
        } catch (Exception e) {
            log.error("Error receiving/parsing message ", e);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("Exception caught in channel handler", cause);
        //ctx.close();
    }

    public List<NgResultListener> getListeners() {
        return listeners;
    }

    /**
     * Set the pairs [server id] and [address] for media proxy hosts
     *
     * @param listeners
     */
    public void setListeners(List<NgResultListener> listeners) {
        this.listeners = listeners;
    }

}
