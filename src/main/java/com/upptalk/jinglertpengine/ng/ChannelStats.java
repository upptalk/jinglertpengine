package com.upptalk.jinglertpengine.ng;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Connection/Channel stats for each server 'connected'
 *
 * @author bhlangonijr
 *         Date: 4/9/14
 *         Time: 11:28 PM
 */
class ChannelStats {
    private final AtomicInteger activeRelayChannels = new AtomicInteger(0);
    private final AtomicInteger sentCommands = new AtomicInteger(0);
    private final AtomicInteger receivedCommands = new AtomicInteger(0);
    private long lastPongTimestamp = 0;
    private long lastPingTimestamp = 0;

    public void incActiveRelayChannels() {
        activeRelayChannels.incrementAndGet();
    }

    public void decActiveRelayChannels() {
        activeRelayChannels.decrementAndGet();
    }

    public int getActiveRelayChannels() {
        return activeRelayChannels.get();
    }

    public long getLastPongTimestamp() {
        return lastPongTimestamp;
    }

    public void setLastPongTimestamp(long lastPongTimestamp) {
        this.lastPongTimestamp = lastPongTimestamp;
    }

    public long getLastPingTimestamp() {
        return lastPingTimestamp;
    }

    public void setLastPingTimestamp(long lastPingTimestamp) {
        this.lastPingTimestamp = lastPingTimestamp;
    }

    public void addSentCommands() {
        this.sentCommands.incrementAndGet();
    }

    public void addReceivedCommands() {
        this.receivedCommands.incrementAndGet();
    }

}
