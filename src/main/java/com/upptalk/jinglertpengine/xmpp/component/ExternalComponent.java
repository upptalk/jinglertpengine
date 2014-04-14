package com.upptalk.jinglertpengine.xmpp.component;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.jivesoftware.whack.ExternalComponentManager;
import org.xmpp.component.AbstractComponent;
import org.xmpp.packet.*;
import org.xmpp.packet.PacketError.Condition;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Jabber External Component
 *
 * @author bhlangonijr
 */
public class ExternalComponent extends AbstractComponent {

    private static final Logger log = Logger.getLogger(ExternalComponent.class);
    private final ConcurrentHashMap<String, List<NamespaceProcessor>> processors = new ConcurrentHashMap<String, List<NamespaceProcessor>>();
    private final List<MessageProcessor> messageProcessors = new ArrayList<MessageProcessor>(1);
    private ExternalComponentManager manager;

    /**
     * The XMPP domain to which this externalComponent is registered to.
     */
    private final String serverDomain;

    /**
     * The name of this externalComponent.
     */
    private final String name;

    /**
     * The description of this externalComponent.
     */
    private final String description;

    private final JID jid;

    /**
     * Create a new externalComponent which provides jinglenodes services
     *
     * @param name         The name of this externalComponent.
     * @param description  The name of this externalComponent.
     * @param serverDomain The XMPP domain to which this externalComponent is registered to.
     */
    public ExternalComponent(final String name, final String description, String serverDomain) {
        super(20, 1000, false);
        this.name = name;
        this.description = description;
        this.serverDomain = serverDomain;
        this.jid = new JID(name + "." + serverDomain);
    }

    public IQ createPacketError(final IQ iq, final Condition condition) {
        final PacketError pe = new PacketError(condition);
        final IQ ret = IQ.createResultIQ(iq);
        ret.setError(pe);
        return ret;
    }

    @Override
    public void send(Packet packet) {
        if (manager != null) {
            if (packet.getFrom() == null)
                packet.setFrom(jid);
            log.debug("Sending XMPP: " + packet.toXML());
            manager.sendPacket(this, packet);
        }
    }

    protected IQ _createPacketError(final Message message, final Condition condition) {
        final PacketError pe = new PacketError(condition);
        final IQ ret = new IQ(IQ.Type.result);
        ret.setID(message.getID());
        ret.setError(pe);
        return ret;
    }

    /**
     * handle a message
     * @param message
     */
    @Override
    protected void handleMessage(Message message) {
        final JID toJid = message.getTo();
        final JID fromJid = message.getFrom();

        if (toJid == null || fromJid == null) {
            send(_createPacketError(message, Condition.bad_request));
            return;
        }

        try {
            for (final MessageProcessor m : messageProcessors) {
                m.processMessage(message);
            }
        } catch (ServiceException e) {
            log.warn("Exception Handling Outgoing Message", e);
        }
    }

    @Override
    protected void handlePresence(Presence presence) {
        //
    }

    @Override
    protected IQ handleIQGet(final IQ iq) throws Exception {
        // Get 'from'.
        final JID jid = iq.getFrom();
        if (null == jid) return null;

        // Get the child element.
        final Element e = iq.getChildElement();
        if (null == e) return null;

        // Get namespace.
        final Namespace namespace = e.getNamespace();
        if (null == namespace) return null;

        // Parse URI from namespace.
        final String ns = namespace.getURI();

        for (final NamespaceProcessor np : processors.get(ns))
            if (null != np) {
                return np.processIQGet(iq);
            }

        return null;
    }

    @Override
    protected IQ handleIQSet(final IQ iq) throws Exception {
        // Get 'from'.
        final JID jid = iq.getFrom();
        if (null == jid) return null;

        // Get the child element.
        final Element e = iq.getChildElement();
        if (null == e) return null;

        // Get namespace.
        final Namespace namespace = e.getNamespace();
        if (null == namespace) return null;

        // Parse URI from namespace.
        final String ns = namespace.getURI();

        for (final NamespaceProcessor np : processors.get(ns))
            if (null != np) {
                return np.processIQSet(iq);
            }

        return null;
    }

    @Override
    protected void handleIQError(final IQ iq) {

        log.debug("Received Error: " + iq.toXML());

        // Get 'to'.
        final JID toJid = iq.getTo();
        if (null == toJid) return;

        // Get 'from'.
        final JID fromJid = iq.getFrom();
        if (null == fromJid) return;

        for (final List<NamespaceProcessor> npl : processors.values()) {
            for (final NamespaceProcessor np : npl)
                np.processIQError(iq);
        }
    }

    @Override
    protected void handleIQResult(final IQ iq) {

        log.debug("Received Result: " + iq.toXML());

        // Get 'to'.
        final JID toJid = iq.getTo();
        if (null == toJid) return;

        // Get 'from'.
        final JID fromJid = iq.getFrom();
        if (null == fromJid) return;

        for (final List<NamespaceProcessor> npl : processors.values()) {
            for (final NamespaceProcessor np : npl)
                np.processIQResult(iq);
        }

    }

    public void addProcessorList(final List<NamespaceProcessor> processorList){
        for (NamespaceProcessor np: processorList)
            addProcessor(np);
    }

    public void addProcessor(final NamespaceProcessor processor) {

        List<NamespaceProcessor> lnp = processors.get(processor.getNamespace());
        if (lnp == null) {
            lnp = new ArrayList<NamespaceProcessor>(1);
            processors.put(processor.getNamespace(), lnp);
        }

        lnp.add(processor);
        log.info("Processor Added: " + processor.getNamespace());

    }

    public void addMessageProcessor(final MessageProcessor messageProcessor) {
        messageProcessors.add(messageProcessor);
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getName() {
        return name;
    }

    public ExternalComponentManager getManager() {
        return manager;
    }

    public void setManager(ExternalComponentManager manager) {
        this.manager = manager;
    }

    public String getServerDomain() {
        return serverDomain;
    }

    public JID getJID() {
        return jid;
    }
}