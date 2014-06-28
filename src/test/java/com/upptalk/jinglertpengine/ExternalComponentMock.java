package com.upptalk.jinglertpengine;

import com.upptalk.jinglertpengine.xmpp.component.ExternalComponent;
import org.apache.log4j.Logger;
import org.eclipse.jetty.util.BlockingArrayQueue;
import org.xmpp.packet.Packet;

import java.util.concurrent.BlockingQueue;

/**
 * @author bhlangonijr
 *         Date: 4/18/14
 *         Time: 3:11 PM
 */
public class ExternalComponentMock extends ExternalComponent {

    private static final Logger log = Logger.getLogger(ExternalComponentMock.class);
    final BlockingQueue<Packet> results = new BlockingArrayQueue<Packet>(10);

    /**
     * Create a new externalComponent which provides jinglenodes services
     *
     * @param name         The name of this externalComponent.
     * @param description  The name of this externalComponent.
     * @param serverDomain The XMPP domain to which this externalComponent is registered to.
     */
    public ExternalComponentMock(String name, String description, String serverDomain) {
        super(name, description, serverDomain);
    }

    @Override
    public void send(Packet packet) {
        log.debug("Sending packet: " + packet);
        results.offer(packet);
    }

    public BlockingQueue<Packet> getResults() {
        return results;
    }
}
