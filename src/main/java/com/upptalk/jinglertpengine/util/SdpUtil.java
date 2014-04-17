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
            /* "v=0\r\n" +
            "o=Sonus_UAC 17438 8530 IN IP4 192.168.100.105\r\n" +
            "s=SIP Media Capabilities\r\n" +
            "c=IN IP4 66.62.162.235\r\n" +
            "t=0 0\r\n" +
            "m=audio 21142 RTP/AVP 18\r\n" +
            "a=rtpmap:18 G729/8000\r\n" +
            "a=sendrecv\r\n" +
            "a=maxptime:20\r\n";*/
            //"v=0\r\no=root 25669 25669 IN IP4 192.168.100.105\r\ns=session\r\nc=IN IP4 192.168.100.105\r\nt=0 0\r\nm=audio 30018 RTP/AVP 8 0 101\r\na=rtpmap:8 PCMA/8000\r\na=rtpmap:0 PCMU/8000\r\na=rtpmap:101 telephone-event/8000\r\na=ptime:20\r\na=sendrecv";
            "v=0\r\n" +
            "o=alice 2890844526 2890844526 IN IP4 127.0.0.1\r\n" +
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


}
