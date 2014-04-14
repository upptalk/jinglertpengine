package com.upptalk.jinglertpengine.xmpp.component;

import org.xmpp.packet.Message;

/**
 * XMPP Message Processor
 * @author bhlangonijr
 */
public interface MessageProcessor {
    /**
     * Process message callback method
     *
     * @param message
     * @throws ServiceException
     */
    public void processMessage(final Message message) throws ServiceException;

}
