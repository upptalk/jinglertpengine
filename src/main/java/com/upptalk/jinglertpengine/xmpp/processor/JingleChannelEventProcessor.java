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
import com.upptalk.jinglertpengine.xmpp.jinglenodes.JingleChannelEvent;
import com.upptalk.jinglertpengine.xmpp.tinder.AchievementEventIQ;
import com.upptalk.jinglertpengine.xmpp.tinder.JingleChannelEventIQ;
import com.upptalk.jinglertpengine.xmpp.tinder.JingleChannelIQ;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;

/**
 * Jingle Channel Event processor
 *
 * @author bhlangonijr
 */
public class JingleChannelEventProcessor implements NamespaceProcessor {

    final static Logger log = Logger.getLogger(JingleChannelEventProcessor.class);
    private final ExternalComponent externalComponent;
    private String achievementService;
    private JID achievementJid;

    public JingleChannelEventProcessor(final ExternalComponent externalComponent) {
        Assert.notNull(externalComponent);
        this.externalComponent = externalComponent;
    }

    public void init() {

    }

    /**
     * Notify about killed channels
     *
     * @para request Channel request IQ
     * @param result Channel Result IQ
     * @param time total time spent by the killed channel
     * @return
     */
    public JingleChannelEventIQ sendChannelEvent(JingleChannelIQ request, JingleChannelIQ result, String time) {
        JingleChannelEventIQ jingleChannelEventIQ = JingleChannelEventIQ.createRequest(result, time);
        getExternalComponent().send(jingleChannelEventIQ);
        log.info("Send channel killed notification with id: " + jingleChannelEventIQ.getID());

        if (getAchievementJid() != null) {
            try {
                AchievementEventIQ achievementEventIQ = AchievementEventIQ.createRequest(request.getFrom(),
                        getAchievementJid(), time);
                getExternalComponent().send(achievementEventIQ); // notify achievement service
            } catch (Exception e) {
                log.error("Error sending achievement " , e);
            }

        }

        return jingleChannelEventIQ;
    }

    @Override
    public IQ processIQGet(IQ iq) {
        log.debug("IQ Get: " + iq);
        return null;
    }

    @Override
    public IQ processIQSet(IQ iq) {
        return null;
    }

    @Override
    public void processIQError(IQ iq) {
        log.debug("IQ Error: " + iq);
    }

    @Override
    public void processIQResult(IQ iq) {
        log.debug("IQ Result: " + iq);
        log.info("Channel killed notification acknowledged for id: " + iq.getID());
    }

    @Override
    public String getNamespace() {
        return JingleChannelEvent.XMLNS;
    }

    public ExternalComponent getExternalComponent() {
        return externalComponent;
    }

    public String getAchievementService() {
        return achievementService;
    }

    public void setAchievementService(String achievementService) {
        this.achievementService = achievementService;
        setAchievementJid(new JID(achievementService));
    }

    public JID getAchievementJid() {
        return achievementJid;
    }

    public void setAchievementJid(JID achievementJid) {
        this.achievementJid = achievementJid;
    }
}
