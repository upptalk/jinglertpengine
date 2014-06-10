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

import com.upptalk.jinglertpengine.util.RandomString;
import com.upptalk.jinglertpengine.xmpp.component.ExternalComponent;
import com.upptalk.jinglertpengine.xmpp.component.NamespaceProcessor;
import com.upptalk.jinglertpengine.xmpp.jinglenodes.JingleChannel;
import com.upptalk.jinglertpengine.xmpp.tinder.JingleChannelIQ;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.xmpp.packet.IQ;
import org.xmpp.packet.PacketError;

/**
 * Jingle Channel processor
 *
 * @author bhlangonijr
 */
public class JingleChannelProcessor implements NamespaceProcessor {

    final static Logger log = Logger.getLogger(JingleChannelProcessor.class);
    private final ExternalComponent externalComponent;
    private JingleChannelSessionManager sessionManager;

    public JingleChannelProcessor(final ExternalComponent externalComponent) {
        Assert.notNull(externalComponent);
        this.externalComponent = externalComponent;
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
        Assert.notNull(getSessionManager());
        JingleChannelIQ iq = null;
        if (log.isDebugEnabled()) {
            log.debug("Received IQ: " + xmppIQ);
        }
        try {
            iq = JingleChannelIQ.fromXml(xmppIQ);
            getSessionManager().createSession(iq.getID(), iq);
        } catch (JingleChannelException e) {
            log.error("Error Processing Jingle Channel request", e);
            sendXmppError(xmppIQ, e.getMessage());
            getSessionManager().destroySession(xmppIQ.getID());
        } catch (Throwable e) {
            log.error("Severe Error Processing Jingle channel: " + xmppIQ, e);
            sendXmppError(xmppIQ, e.getMessage());
            getSessionManager().destroySession(xmppIQ.getID());
        }

        //should be returned by JingleChannelSessionManager
        return null;//IQ.createResultIQ(iq);

    }

    /**
     * Send an error result to requester
     * @param request
     * @param error
     * @return created result IQ
     */
    public IQ sendChannelError(JingleChannelIQ request, String error) {
        IQ iq = JingleChannelIQ.createErrorResult(request, PacketError.Condition.internal_server_error,
                error);
        getExternalComponent().send(iq);
        return iq;
    }

    /**
     * Send an error result to requester
     * @param request
     * @param error
     * @return created result IQ
     */
    public IQ sendXmppError(IQ request, String error) {
        IQ result = IQ.createResultIQ(request);
        result.setError(new PacketError(PacketError.Condition.internal_server_error, PacketError.Type.cancel, error));
        getExternalComponent().send(result);
        return result;
    }

    /**
     * Send the channel IQ result to requester
     * @param request
     * @param host
     * @param protocol
     * @param localPort
     * @param remotePort
     * @return
     */
    public JingleChannelIQ sendChannelResult(JingleChannelIQ request, String host,
                                             String protocol, Integer localPort, Integer remotePort) {
        JingleChannelIQ iq = JingleChannelIQ.createResult(request, host, protocol,
                localPort, remotePort, RandomString.getCookie());
        getExternalComponent().send(iq);
        return iq;
    }

    @Override
    public IQ processIQGet(IQ iq) {
        log.debug("IQ Get: " + iq);
        return processIQ(iq);
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

    public ExternalComponent getExternalComponent() {
        return externalComponent;
    }

    @Required
    public void setSessionManager(JingleChannelSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public JingleChannelSessionManager getSessionManager() {
        return sessionManager;
    }

}
