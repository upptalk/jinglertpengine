package com.upptalk.jinglertpengine.xmpp.tinder;

import com.upptalk.jinglertpengine.xmpp.jinglenodes.JingleChannel;
import com.upptalk.jinglertpengine.xmpp.tinder.parser.XStreamIQ;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xmpp.packet.IQ;
import org.xmpp.packet.PacketError;

import java.io.StringReader;

/**
 * Jingle Channel IQ
 *
 * This is the response for #channel allocation requests specified by jinglenodes
 *
 * @author bhlangonijr
 */
public class JingleChannelIQ extends XStreamIQ<JingleChannel> {

    final static Logger log = Logger.getLogger(JingleChannelIQ.class);
    private final JingleChannel jingleChannel;

    public JingleChannelIQ(final JingleChannel element) {
        this.setType(IQ.Type.get);
        this.jingleChannel = element;
        final Document originalDoc;
        try {
            originalDoc = new SAXReader().read(new StringReader(element.toString()));
            final Element e = originalDoc.getRootElement().createCopy();
            this.element.add(e);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    public static JingleChannelIQ fromXml(final IQ iq) {
        Element e = iq.getChildElement();
        if (e == null) {
            e = iq.getElement();
        }
        if (e != null) {
            if (!"channel".equals(e.getName())) {
                e = e.element("channel");
            }
            final String child = e.asXML().replace("\n", "");
            final JingleChannel j = (JingleChannel) JingleChannelIQ.getStream().fromXML(child);

            final JingleChannelIQ jingleChannelIQ = new JingleChannelIQ(j);
            jingleChannelIQ.setTo(iq.getTo());
            jingleChannelIQ.setFrom(iq.getFrom());
            jingleChannelIQ.setID(iq.getID());
            jingleChannelIQ.setType(iq.getType());

            return jingleChannelIQ;
        }
        return null;
    }


    /**
     * Creates a JingleChannelIQ result
     * @see <a href="http://xmpp.org/extensions/xep-0278.html">http://jabber.org/protocol/jinglenodes#channel</a>
     * @param iq
     * @return
     */
    public static JingleChannelIQ createResult(final JingleChannelIQ iq, String host, String protocol,
                                               Integer localport, Integer remoteport, String channelId) {
        final JingleChannel j = new JingleChannel(protocol, host, localport, remoteport, channelId);

        final JingleChannelIQ jingleChannelIQ = new JingleChannelIQ(j);
        jingleChannelIQ.setTo(iq.getFrom());
        jingleChannelIQ.setFrom(iq.getTo());
        jingleChannelIQ.setID(iq.getID());
        jingleChannelIQ.setType(iq.getType());

        return jingleChannelIQ;

    }

    /**
     * Creates an IQ error result in case channel can't be allocated
     *
     * @param iq Original request IQ
     * @param condition Error condition
     * @param error Error description
     * @return Result IQ
     */
    public static IQ createErrorResult(JingleChannelIQ iq, PacketError.Condition condition, String error) {
        IQ result = IQ.createResultIQ(iq);
        result.setError(new PacketError(condition, PacketError.Type.cancel, error));
        return result;
    };


    public static JingleChannelIQ clone(final JingleChannelIQ iq) {
        final JingleChannelIQ jingleChannelIQ = new JingleChannelIQ(iq.getJingleChannel());
        jingleChannelIQ.setTo(iq.getTo());
        jingleChannelIQ.setFrom(iq.getFrom());
        jingleChannelIQ.setID(iq.getID());
        jingleChannelIQ.setType(iq.getType());
        return jingleChannelIQ;
    }

    public JingleChannel getJingleChannel() {
        return jingleChannel;
    }


}
