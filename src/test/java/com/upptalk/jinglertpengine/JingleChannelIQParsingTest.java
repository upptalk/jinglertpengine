package com.upptalk.jinglertpengine;

import com.upptalk.jinglertpengine.xmpp.jinglenodes.JingleChannel;
import com.upptalk.jinglertpengine.xmpp.tinder.JingleChannelIQ;
import org.junit.Test;
import org.xmpp.packet.IQ;

import static org.junit.Assert.assertEquals;

/**
 * @author bhlangonijr
 *         Date: 4/13/14
 *         Time: 9:38 PM
 */
public class JingleChannelIQParsingTest {


    String jingleChannelIQString = "<iq type=\"get\" id=\"764-35019616\" from=\"+16109862250@sipin3601.ym.ms\" to=\"+16109862250@sjin360.ym.ms\">" +
            "<channel xmlns=\"http://jabber.org/protocol/jinglenodes#channel\" protocol=\"udp\"></channel>" +
            "</iq>";
    @Test
    public void testJingleChannelCreation() {

        JingleChannel channel = new JingleChannel();
        channel.setProtocol(JingleChannel.UDP);
                JingleChannelIQ iq = new JingleChannelIQ(channel);
        iq.setFrom("+16109862250@sipin3601.ym.ms");
        iq.setType(IQ.Type.get);
        iq.setTo("+16109862250@sjin360.ym.ms");
        iq.setID("764-35019616");

        System.out.println(iq.toXML());

        assertEquals(jingleChannelIQString, iq.toXML());

    }



}
