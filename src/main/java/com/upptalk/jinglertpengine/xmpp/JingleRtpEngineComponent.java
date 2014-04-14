package com.upptalk.jinglertpengine.xmpp;

import com.upptalk.jinglertpengine.xmpp.component.ExternalComponent;
import com.upptalk.jinglertpengine.xmpp.jinglenodes.JingleChannel;
import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.xmpp.packet.IQ;
import org.xmpp.packet.Presence;

/**
 * Main Component for handling XMPP requests
 *
 * @author bhlangonijr
 *         Date: 5/9/13
 *         Time: 1:00 PM
 */
public class JingleRtpEngineComponent extends ExternalComponent {
    final static Logger log = Logger.getLogger(JingleRtpEngineComponent.class);
    /**
     * Create a new externalComponent which provides jinglenodes services
     *
     * @param name         The name of this externalComponent.
     * @param description  The name of this externalComponent.
     * @param serverDomain The XMPP domain to which this externalComponent is registered to.
     */
    public JingleRtpEngineComponent(String name, String description, String serverDomain) {
        super(name, description, serverDomain);
    }

    //for testing
    @Override
    protected void handlePresence(Presence presence) {

        log.debug("Presence: " + presence);

        try {

            if (presence.getType() != null && presence.getType().equals(Presence.Type.subscribe)) {

                Presence p = new Presence();
                p.setType(Presence.Type.subscribed);
                p.setTo(presence.getFrom());
                p.setFrom(presence.getTo());

                log.debug("Presence subscribe response: " + p);

                send(p);

            } else if (presence.getType() != null && presence.getType().equals(Presence.Type.probe)) {

                Presence p = new Presence();
                p.setTo(presence.getFrom());
                p.setFrom(presence.getTo());
                p.setShow(Presence.Show.chat);
                p.setPriority(1);
                p.setStatus("Upptalk Jinglenodes");

                log.debug("Presence probe response: " + p);

                send(p);

            } else if (presence.getType() == null) {

                Presence p = presence.createCopy();
                p.setTo(presence.getFrom());
                p.setFrom(presence.getTo());

                log.debug("Presence general response: " + p);

                send(p);
            }
        } catch (Exception e) {
            log.error("Error while processing presence ", e);
        }
    }

    @Override
    protected IQ handleDiscoInfo(IQ iq) {
        final IQ replyPacket = IQ.createResultIQ(iq);
        final Element responseElement = replyPacket.setChildElement("query",
                NAMESPACE_DISCO_INFO);

        // identity
        responseElement.addElement("identity").addAttribute("category",
                discoInfoIdentityCategory()).addAttribute("type",
                discoInfoIdentityCategoryType())
                .addAttribute("name", getName());
        // features
        responseElement.addElement("feature").addAttribute("var",
                NAMESPACE_DISCO_INFO);
        responseElement.addElement("feature").addAttribute("var",
                NAMESPACE_XMPP_PING);
        responseElement.addElement("feature").addAttribute("var",
                NAMESPACE_LAST_ACTIVITY);
        responseElement.addElement("feature").addAttribute("var",
                NAMESPACE_ENTITY_TIME);
        responseElement.addElement("feature").addAttribute("var",
                JingleChannel.XMLNS);
        for (final String feature : discoInfoFeatureNamespaces()) {
            responseElement.addElement("feature").addAttribute("var", feature);
        }
        return replyPacket;
    }


}
