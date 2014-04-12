package com.upptalk.jinglertpengine.ng;

import com.upptalk.jinglertpengine.ng.protocol.NgCommand;

import java.net.InetSocketAddress;

/**
 * Media proxy listener for sent commands
 *
 * @author bhlangonijr
 *         Date: 4/10/14
 *         Time: 10:57 AM
 */
public interface NgCommandListener {

    /**
     * Notifies when a command is sent to media proxy
     * @param command
     */
    void sent(NgCommand command, InetSocketAddress server);

}
