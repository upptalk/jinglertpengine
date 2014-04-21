package com.upptalk.jinglertpengine;

import com.upptalk.jinglertpengine.ng.protocol.Sdp;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author bhlangonijr
 *         Date: 4/18/14
 *         Time: 5:43 PM
 */
public class SdpTest {


    static final String sdp1 = "v=0\n"+
            "o=root 123 123 IN IP4 127.0.0.1\n"+
            "s=stream\n"+
            "c=IN IP4 192.168.100.105 \n"+
            "t=0 0 \n"+
            "a=ice-lite\n"+
            "m=audio 30000 RTP/AVP 0 97\n"+
            "a=rtpmap:97 iLBC/8000\n"+
            "a=sendrecv\n"+
            "a=rtcp:30001\n"+
            "a=ice-ufrag:NW2PkF0P\n"+
            "a=ice-pwd:foHblBGeEx8E1gst93WNNhJAdLnv\n"+
            "a=candidate:j1z42oFdvAqSWWVw 1 UDP 2130706432 192.168.100.105 30000 typ host\n"+
            "a=candidate:j1z42oFdvAqSWWVw 2 UDP 2130706431 192.168.100.105 30001 typ host";

    static final String sdp2 = "v=0\n" +
            "o=root 123 123 IN IP4 127.0.0.1\n" +
            "s=stream\n" +
            "c=IN IP4 192.168.100.105 \n" +
            "t=0 0 \n" +
            "a=ice-lite\n" +
            "m=audio 30002 RTP/AVP 0 97\n" +
            "a=rtpmap:97 iLBC/8000\n" +
            "a=sendrecv\n" +
            "a=rtcp:30003\n" +
            "a=ice-ufrag:qniDLYO4\n" +
            "a=ice-pwd:zshbXpPYDhpMiky5BhDN00golz15\n" +
            "a=candidate:j1z42oFdvAqSWWVw 1 UDP 2130706432 192.168.100.105 30002 typ host\n" +
            "a=candidate:j1z42oFdvAqSWWVw 2 UDP 2130706431 192.168.100.105 30003 typ host";

    static final String sdp3 = "sdp=v=0\n" +
            "o=root 127 127 IN IP4 127.0.0.1\n" +
            "s=stream\n" +
            "c=IN IP4 192.168.100.105\n" +
            "t=0 0\n" +
            "a=ice-lite\n" +
            "m=audio 30066 RTP/AVP\n" +
            "a=sendrecv\n" +
            "a=rtcp:30067\n" +
            "a=ice-ufrag:Lv2oQHxb\n" +
            "a=ice-pwd:yuxk5Owm85le2uvAbw0F1wdK2f8Q\n" +
            "a=candidate:GFnLlrmXWAQt5BqS 1 UDP 2130706432 192.168.100.105 30066 typ host\n" +
            "a=candidate:GFnLlrmXWAQt5BqS 2 UDP 2130706431 192.168.100.105 30067 typ host";

    @Test
    public void testParsing1() {

        Sdp sdp = Sdp.fromSdp(sdp1);

        assertEquals(30000, sdp.getMediaPort());
        assertEquals("192.168.100.105", sdp.getConnectionAddress());
        assertEquals("root", sdp.getOriginUsername());

    }

    @Test
    public void testParsing2() {

        Sdp sdp = Sdp.fromSdp(sdp2);

        assertEquals(30002, sdp.getMediaPort());
        assertEquals("192.168.100.105", sdp.getConnectionAddress());
        assertEquals("root", sdp.getOriginUsername());

    }

    @Test
    public void testParsing3() {

        Sdp sdp = Sdp.fromSdp(sdp3);

        assertEquals(30066, sdp.getMediaPort());
        assertEquals("192.168.100.105", sdp.getConnectionAddress());
        assertEquals("root", sdp.getOriginUsername());

    }

    @Test
    public void testCreation() {

        Sdp sdp = Sdp.createTwoAudioRtpIp4Basic("127.0.0.1", "127.0.0.1", 49170);
        assertEquals("v=0\r\n" +
                "o=root 123 123 IN IP4 127.0.0.1\r\n" +
                "s=stream\r\n" +
                "c=IN IP4 127.0.0.1\r\n" +
                "t=0 0\r\n" +
                "m=audio 49170 RTP/AVP\r\n", sdp.toSdp());

    }


}
