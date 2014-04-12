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
