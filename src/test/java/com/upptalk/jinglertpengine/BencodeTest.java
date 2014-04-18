package com.upptalk.jinglertpengine;

import com.upptalk.jinglertpengine.ng.protocol.NgCommand;
import com.upptalk.jinglertpengine.ng.protocol.NgCommandType;
import com.upptalk.jinglertpengine.ng.protocol.NgResult;
import com.upptalk.jinglertpengine.util.Bencode;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author bhlangonijr
 *         Date: 4/6/14
 *         Time: 9:37 AM
 */
public class BencodeTest {

    static final String offerResult = "8ss0orl d3:sdp376:v=0\n" +
            "o=root 123 123 IN IP4 127.0.0.1\n" +
            "s=stream\n" +
            "c=IN IP4 192.168.100.105 \n" +
            "t=0 0 \n" +
            "a=ice-lite\n" +
            "m=audio 30014 RTP/AVP 0 97\n" +
            "a=rtpmap:97 iLBC/8000\n" +
            "a=sendrecv\n" +
            "a=rtcp:30015\n" +
            "a=ice-ufrag:Q8qyLLxS\n" +
            "a=ice-pwd:ocKHbXOta29tAixEekJXR6VFcjdY\n" +
            "a=candidate:j1z42oFdvAqSWWVw 1 UDP 2130706432 192.168.100.105 30014 typ host\n" +
            "a=candidate:j1z42oFdvAqSWWVw 2 UDP 2130706431 192.168.100.105 30015 typ host\n" +
            "6:result2:oke";

    static final String sdp = "v=0\n" +
            "o=root 123 123 IN IP4 127.0.0.1\n" +
            "s=stream\n" +
            "c=IN IP4 192.168.100.105 \n" +
            "t=0 0 \n" +
            "a=ice-lite\n" +
            "m=audio 30014 RTP/AVP 0 97\n" +
            "a=rtpmap:97 iLBC/8000\n" +
            "a=sendrecv\n" +
            "a=rtcp:30015\n" +
            "a=ice-ufrag:Q8qyLLxS\n" +
            "a=ice-pwd:ocKHbXOta29tAixEekJXR6VFcjdY\n" +
            "a=candidate:j1z42oFdvAqSWWVw 1 UDP 2130706432 192.168.100.105 30014 typ host\n" +
            "a=candidate:j1z42oFdvAqSWWVw 2 UDP 2130706431 192.168.100.105 30015 typ host\n";


    @Test
    public void testDecode() throws Exception {
        System.out.println(sdp.length());
        NgResult result = NgResult.fromBencode(new ByteArrayInputStream(offerResult.getBytes()));

        System.out.println(result.toString());

    }

    @Test
    public void testEncode() throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        map.put("command","ping");
        String ping = Bencode.encode(map);
        assertEquals("d7:command4:pinge", ping);
    }

    @Test
    public void testCommandPingEncode() throws Exception {
        NgCommand ping = NgCommand.builder()
                .setCookie("5323_1")
                .setNgCommandType(NgCommandType.ping)
                .build();
        assertEquals("5323_1 d7:command4:pinge", ping.toBencode());
    }

    @Test
    public void testResultPongDecode() throws Exception {
        NgResult pong = NgResult.fromBencode(new ByteArrayInputStream("5323_1 d6:result4:ponge".getBytes()));
        assertEquals("5323_1 d6:result4:ponge", pong.toBencode());
    }


}
