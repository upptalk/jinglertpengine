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
import com.upptalk.jinglertpengine.xmpp.tinder.parser.XStreamIQ;
import org.dom4j.Namespace;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XStreamAlias("channel")
@XmlRootElement(name = "channel")
public class JingleChannelEvent {

    public final static String NAME = "channel";
    public static final Namespace Q_NAMESPACE = new Namespace("", "http://jabber.org/protocol/jinglenodes#event");

    @XStreamAsAttribute
    @XStreamAlias("xmlns")
    @XmlAttribute(name = "xmlns")
    public final String NAMESPACE = "http://jabber.org/protocol/jinglenodes#event";
    public static final String XMLNS = "http://jabber.org/protocol/jinglenodes#event";
    public static final String KILLED = "killed";

    @XStreamAsAttribute
    @XmlAttribute
    private String event = KILLED;
    @XStreamAsAttribute
    @XmlAttribute
    private String time;
    @XStreamAsAttribute
    @XmlAttribute
    private String id;

    public JingleChannelEvent(String time, String id) {
        this.time = time;
        this.id = id;
    }

    public JingleChannelEvent() {}

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String toString() {
        return XStreamIQ.getStream().toXML(this);
    }

}
