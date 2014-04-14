package com.upptalk.jinglertpengine.xmpp.component;

import org.xmpp.packet.IQ;

/**
 * Result and Error listener
 * @author bhlangonijr
 */
public interface ResultAndErrorListener {
    public void handleIQError(final IQ iq);

    public void handleIQResult(final IQ iq);
}
