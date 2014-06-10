package com.upptalk.jinglertpengine.xmpp.tinder;

import com.upptalk.jinglertpengine.util.XmlParser;
import com.upptalk.jinglertpengine.xmpp.jinglenodes.JingleChannelEvent;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xmpp.packet.IQ;

import java.io.StringReader;

/**
 * Jingle Channel Event IQ
 *
 * Notify about killed channels
 *
 * @author bhlangonijr
 */
public class JingleChannelEventIQ extends IQ {

    final static Logger log = Logger.getLogger(JingleChannelEventIQ.class);
    private final JingleChannelEvent jingleChannelEvent;

    final static XmlParser parser = new XmlParser();

    public final static XmlParser getParser() {
        return parser;
    }

    public JingleChannelEventIQ(final JingleChannelEvent element) {
        this.setType(Type.set);
        this.jingleChannelEvent = element;
        final Document originalDoc;
        try {
            originalDoc = new SAXReader().read(new StringReader(element.toString()));
            final Element e = originalDoc.getRootElement().createCopy();
            this.element.add(e);
        } catch (DocumentException e) {
            log.error("Error creating IQ", e);
        }
    }

    public static JingleChannelEventIQ fromXml(final IQ iq) {
        Element e = iq.getChildElement();
        if (e == null) {
            e = iq.getElement();
        }
        if (e != null) {
            if (!"channel".equals(e.getName())) {
                e = e.element("channel");
            }
            final String child = e.asXML().replace("\n", "");
            final JingleChannelEvent j = (JingleChannelEvent) getParser().fromXML(child);

            final JingleChannelEventIQ jingleChannelEventIQ = new JingleChannelEventIQ(j);
            jingleChannelEventIQ.setTo(iq.getTo());
            jingleChannelEventIQ.setFrom(iq.getFrom());
            jingleChannelEventIQ.setID(iq.getID());
            jingleChannelEventIQ.setType(iq.getType());

            return jingleChannelEventIQ;
        }
        return null;
    }


    /**
     * Creates a JingleChannelEventIQ set
     *
     * @param time time used by the channel
     * @return
     */
    public static JingleChannelEventIQ createRequest(final JingleChannelIQ channelResultIq, final String time) {
        final JingleChannelEvent j = new JingleChannelEvent(time, channelResultIq.getJingleChannel().getId());

        final JingleChannelEventIQ jingleChannelIQ = new JingleChannelEventIQ(j);
        jingleChannelIQ.setTo(channelResultIq.getTo());
        jingleChannelIQ.setFrom(channelResultIq.getFrom());
        jingleChannelIQ.setType(channelResultIq.getType());

        return jingleChannelIQ;

    }

    public static JingleChannelEventIQ clone(final JingleChannelEventIQ iq) {
        final JingleChannelEventIQ jingleChannelIQ = new JingleChannelEventIQ(iq.getJingleChannelEvent());
        jingleChannelIQ.setTo(iq.getTo());
        jingleChannelIQ.setFrom(iq.getFrom());
        jingleChannelIQ.setID(iq.getID());
        jingleChannelIQ.setType(iq.getType());
        return jingleChannelIQ;
    }

    public JingleChannelEvent getJingleChannelEvent() {
        return jingleChannelEvent;
    }


}
