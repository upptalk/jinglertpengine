package com.upptalk.jinglertpengine.xmpp.processor;

import com.upptalk.jinglertpengine.xmpp.tinder.JingleChannelIQ;
import org.xmpp.packet.IQ;

import java.io.Serializable;

/**
 * JingleChannel session
 *
 * @author bhlangonijr
 *         Date: 5/16/13
 *         Time: 3:09 PM
 */
public class JingleChannelSession implements Serializable {

    private final String id;
    private final JingleChannelIQ requestIQ;
    private JingleChannelIQ responseIQ;
    private final long timestamp = System.currentTimeMillis();

    private JingleChannelSession(String id, JingleChannelIQ requestIQ) {
        this.id = id;
        this.requestIQ = requestIQ;
    }

    public String getId() {
        return id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public IQ getRequestIQ() {
        return requestIQ;
    }

    public JingleChannelIQ getResponseIQ() {
        return responseIQ;
    }

    public void setResponseIQ(JingleChannelIQ responseIQ) {
        this.responseIQ = responseIQ;
    }


    /**
     * Creates a new Jingle Relay Channel Session
     *
     * @param id
     * @param requestIQ
     * @return The Channel Relay Session
     */
    public static JingleChannelSession create(String id, JingleChannelIQ requestIQ) {

        JingleChannelSession s = new JingleChannelSession(id, requestIQ);



        return s;
    }

}
