package com.upptalk.jinglertpengine.xmpp.component;

import org.xmpp.packet.IQ;

/**
 * Namespace processor
 * @author bhlangonijr
 *
 */
public interface NamespaceProcessor {
    public IQ processIQGet(final IQ iq);

    public IQ processIQSet(final IQ iq);

    public void processIQError(final IQ iq);

    public void processIQResult(final IQ iq);

    public String getNamespace();
}
