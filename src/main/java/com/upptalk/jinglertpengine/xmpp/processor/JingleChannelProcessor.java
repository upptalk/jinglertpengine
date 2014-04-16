/*
 * Copyright (C) 2011 - Jingle Nodes - Yuilop - Neppo
 *
 *   This file is part of Switji (http://jinglenodes.org)
 *
 *   Switji is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   Switji is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with MjSip; if not, write to the Free Software
 *   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *   Author(s):
 *   Benhur Langoni (bhlangonijr@gmail.com)
 *   Thiago Camargo (barata7@gmail.com)
 */

package com.upptalk.jinglertpengine.xmpp.processor;

import com.upptalk.jinglertpengine.xmpp.component.ExternalComponent;
import com.upptalk.jinglertpengine.xmpp.component.NamespaceProcessor;
import com.upptalk.jinglertpengine.xmpp.jinglenodes.JingleChannel;
import com.upptalk.jinglertpengine.xmpp.tinder.JingleChannelIQ;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;
import org.xmpp.packet.IQ;

/**
 * Jingle RTPEngine processor
 *
 * @author bhlangonijr
 */
public class JingleChannelProcessor implements NamespaceProcessor {

    final static Logger log = Logger.getLogger(JingleChannelProcessor.class);
    private ExternalComponent externalComponent;
    private String localIp;
    private final JingleChannelSessionManager sessionManager;

    private static final int DEFAULT_RTP_LOCAL_PORT_START = 10000;
    private static final int DEFAULT_RTP_LOCAL_PORT_END = 60000;

    private int rtpStartPort = DEFAULT_RTP_LOCAL_PORT_START;
    private int rtpEndPort = DEFAULT_RTP_LOCAL_PORT_END;

    public JingleChannelProcessor(final JingleChannelSessionManager sessionManager) {
        Assert.notNull(sessionManager);
        this.sessionManager = sessionManager;
    }

    public void init() {

    }

    /**
     * Process the incoming channel request
     *
     * @param xmppIQ
     * @return
     */
    public IQ processIQ(final IQ xmppIQ) {
        JingleChannelIQ iq = null;
        if (log.isDebugEnabled()) {
            log.debug("Received IQ: " + xmppIQ);
        }
        try {
            iq = JingleChannelIQ.fromXml(xmppIQ);
            JingleChannelSession session = sessionManager.createSession(iq.getID(), iq);
            processJingleChannel(session);
        } catch (JingleChannelException e) {
            log.error("Error Processing Jingle Channel request", e);
        } catch (Throwable e) {
            log.error("Severe Error Processing Jingle channel: " + xmppIQ, e);
        }

        //should be return by JingleChannelSessionManager
        return null;//IQ.createResultIQ(iq);

    }

    private void processJingleChannel(final JingleChannelSession session) throws JingleChannelException {

        //TODO

    }

    @Override
    public IQ processIQGet(IQ iq) {
        log.debug("IQ Get: " + iq);
        return null;
    }

    @Override
    public IQ processIQSet(IQ iq) {
        return processIQ(iq);
    }

    @Override
    public void processIQError(IQ iq) {
        log.debug("IQ Error: " + iq);
    }

    @Override
    public void processIQResult(IQ iq) {
        log.debug("IQ Result: " + iq);
    }

    @Override
    public String getNamespace() {
        return JingleChannel.XMLNS;
    }

    public void setExternalComponent(ExternalComponent externalComponent) {
        this.externalComponent = externalComponent;
    }

    public ExternalComponent getExternalComponent() {
        return externalComponent;
    }

    public String getLocalIp() {
        return localIp;
    }

    public void setLocalIp(String localIp) {
        this.localIp = localIp;
    }

    public int getRtpStartPort() {
        return rtpStartPort;
    }

    public void setRtpStartPort(int rtpStartPort) {
        this.rtpStartPort = rtpStartPort;
    }

    public int getRtpEndPort() {
        return rtpEndPort;
    }

    public void setRtpEndPort(int rtpEndPort) {
        this.rtpEndPort = rtpEndPort;
    }

    public JingleChannelSessionManager getSessionManager() {
        return sessionManager;
    }
}
