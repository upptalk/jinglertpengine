package com.upptalk.jinglertpengine.xmpp.tinder;

import com.upptalk.jinglertpengine.util.XmlParser;
import com.upptalk.jinglertpengine.xmpp.achievement.AchievementEvent;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;

import java.io.StringReader;

/**
 * Achievement Event IQ
 *
 * Notify about killed channels the achievements service
 *
 * @author bhlangonijr
 */
public class AchievementEventIQ extends IQ {

    final static Logger log = Logger.getLogger(AchievementEventIQ.class);
    private final AchievementEvent achievementEvent;

    final static XmlParser parser = new XmlParser();
    static {
        getParser().processAnnotations(AchievementEvent.class);
    }
    public final static XmlParser getParser() {
        return parser;
    }

    public AchievementEventIQ(final AchievementEvent element) {
        this.setType(Type.set);
        this.achievementEvent = element;
        final Document originalDoc;
        try {
            originalDoc = new SAXReader().read(new StringReader(element.toString()));
            final Element e = originalDoc.getRootElement().createCopy();
            this.element.add(e);
        } catch (DocumentException e) {
            log.error("Error creating IQ", e);
        }
    }

    /**
     * Creates a AchievementEventIQ set
     *
     * @param time time used by the channel
     * @return
     */
    public static AchievementEventIQ createRequest(final JID from, final JID to, final String time) {

        final AchievementEvent e = new AchievementEvent();

        final AchievementEventIQ achievementEventIQ = new AchievementEventIQ(e);
        achievementEventIQ.setTo(to);
        achievementEventIQ.setFrom(from);
        achievementEventIQ.setType(Type.set);

        final Element event = achievementEventIQ.getChildElement().addElement("event");
        event.addAttribute("key", "call_killed");
        event.addAttribute("value", time);
        event.addNamespace("", "");

        return achievementEventIQ;

    }


    public AchievementEvent getAchievementEvent() {
        return achievementEvent;
    }


}
