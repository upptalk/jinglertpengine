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

package com.upptalk.jinglertpengine.xmpp.jinglenodes;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.upptalk.jinglertpengine.xmpp.tinder.JingleChannelIQ;
import org.dom4j.Namespace;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XStreamAlias("channel")
@XmlRootElement(name = "channel")
public class JingleChannel {

    public final static String NAME = "channel";
    public static final Namespace Q_NAMESPACE = new Namespace("", "http://jabber.org/protocol/jinglenodes#channel");

    @XStreamAsAttribute
    @XStreamAlias("xmlns")
    @XmlAttribute(name = "xmlns")
    public final String NAMESPACE = "http://jabber.org/protocol/jinglenodes#channel";
    public static final String XMLNS = "http://jabber.org/protocol/jinglenodes#channel";
    public static final String UDP= "udp";
    public static final String TCP= "tcp";

    @XStreamAsAttribute
    @XmlAttribute
    private String protocol = UDP;
    @XStreamAsAttribute
    @XmlAttribute
    private String host;
    @XStreamAsAttribute
    @XmlAttribute
    private Integer localport;
    @XStreamAsAttribute
    @XmlAttribute
    private Integer remoteport;
    @XStreamAsAttribute
    @XmlAttribute
    private String id;

    public JingleChannel(String protocol, String host, Integer localport, Integer remoteport, String id) {
        this.protocol = protocol;
        this.host = host;
        this.localport = localport;
        this.remoteport = remoteport;
        this.id = id;
    }

    public JingleChannel() {}

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getLocalport() {
        return localport;
    }

    public void setLocalport(Integer localport) {
        this.localport = localport;
    }

    public Integer getRemoteport() {
        return remoteport;
    }

    public void setRemoteport(Integer remoteport) {
        this.remoteport = remoteport;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String toString() {
        return JingleChannelIQ.getParser().toXML(this);
    }

}
