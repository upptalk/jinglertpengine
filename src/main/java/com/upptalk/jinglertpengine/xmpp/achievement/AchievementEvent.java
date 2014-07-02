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

package com.upptalk.jinglertpengine.xmpp.achievement;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.upptalk.jinglertpengine.xmpp.tinder.AchievementEventIQ;
import org.dom4j.Namespace;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XStreamAlias("query")
@XmlRootElement(name = "query")
public class AchievementEvent {

    public final static String NAME = "query";
    public static final Namespace Q_NAMESPACE = new Namespace("", "achievement#event");

    @XStreamAsAttribute
    @XStreamAlias("xmlns")
    @XmlAttribute(name = "xmlns")
    public final String NAMESPACE = "achievement#event";
    public static final String XMLNS = "achievement#event";
    public static final String KILLED = "killed";

    public AchievementEvent() {}


    public String toString() {
        return AchievementEventIQ.getParser().toXML(this);
    }

}
