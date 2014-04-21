package com.upptalk.jinglertpengine.util;

import com.upptalk.jinglertpengine.ng.protocol.Sdp;
import org.xmpp.packet.IQ;

/**
 * SDP utility functions
 *
 * @author bhlangonijr
 *         Date: 4/16/14
 *         Time: 8:30 PM
 */
public class SdpUtil {

    public static final Sdp fakeSdp = Sdp.createTwoAudioRtpIp4Basic("127.0.0.1", "127.0.0.1", 49100);

    /**
     * Generates a fake sip from-tag
     *
     * @param iq
     * @return from-tag string
     */
    public static String getFakeFromTag(IQ iq) {
        return iq.getFrom().getNode().replace("+", "00") + "." + iq.getID();
    }

    /**
     * Generates a fake sip to-tag
     *
     * @param iq
     * @return to-tag string
     */
    public static String getFakeToTag(IQ iq) {
        return iq.getTo().getNode().replace("+", "00") + "." + iq.getID();
    }

}
