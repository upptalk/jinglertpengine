package com.upptalk.jinglertpengine.util;

import org.xmpp.packet.IQ;

/**
 * SDP utility functions
 *
 * @author bhlangonijr
 *         Date: 4/16/14
 *         Time: 8:30 PM
 */
public class SdpUtil {

    public static final String fakeSdp =
            "v=0\r\n" +
            "o=root 123 123 IN IP4 127.0.0.1\r\n" +
            "s=stream\r\n" +
            "c=IN IP4 127.0.0.1 \r\n" +
            "t=0 0 \r\n" +
            "m=audio 49170 RTP/AVP 0 97\r\n" +
            "a=rtpmap:97 iLBC/8000\r\n" +
            "a=sendrecv";

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
