package com.upptalk.jinglertpengine;

import com.upptalk.jinglertpengine.xmpp.jinglenodes.JingleChannel;
import com.upptalk.jinglertpengine.xmpp.tinder.AchievementEventIQ;
import com.upptalk.jinglertpengine.xmpp.tinder.JingleChannelEventIQ;
import com.upptalk.jinglertpengine.xmpp.tinder.JingleChannelIQ;
import org.junit.Test;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;

import static junit.framework.Assert.assertTrue;
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

    String getJingleChannelResultIQString = "<iq type=\"result\" id=\"764-35019616\" to=\"+16109862250@sipin3601.ym.ms\" " +
            "from=\"+16109862250@sjin360.ym.ms\"><channel xmlns=\"http://jabber.org/protocol/jinglenodes#channel\" " +
            "host=\"189.20.30.1\" localport=\"2300\" remoteport=\"40000\" id=\"1n98c318n\"></channel></iq>";

    String getJingleChannelEventIQString1 = "<iq type=\"get\" id=\"578-2\" to=\"+16109862250@sipin3601.ym.ms\" " +
            "from=\"+16109862250@sjin360.ym.ms\"><channel xmlns=\"http://jabber.org/protocol/jinglenodes#event\" " +
            "event=\"killed\" time=\"30\" id=\"1n98c318n\"></channel></iq>";

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



    @Test
    public void testJingleChannelEventCreation() {

        JingleChannel channelRequest = new JingleChannel();
        channelRequest.setProtocol(JingleChannel.UDP);
        JingleChannelIQ iqRequest = new JingleChannelIQ(channelRequest);
        iqRequest.setFrom("+16109862250@sipin3601.ym.ms");
        iqRequest.setType(IQ.Type.get);
        iqRequest.setTo("+16109862250@sjin360.ym.ms");
        iqRequest.setID("764-35019616");

        System.out.println(iqRequest.toXML());

        assertEquals(jingleChannelIQString, iqRequest.toXML());

        JingleChannelIQ iqResult = JingleChannelIQ.createResult(iqRequest, "189.20.30.1", null, 2300, 40000, "1n98c318n");

        System.out.println(iqResult.toXML());
        assertEquals(getJingleChannelResultIQString, iqResult.toXML());

        IQ iqEvent = JingleChannelEventIQ.createRequest(iqResult, "30");

        System.out.println(iqEvent.toXML());

        JingleChannelEventIQ iqEvent2 = JingleChannelEventIQ.fromXml(iqEvent);

        System.out.println(iqEvent2);
        //assertEquals(getJingleChannelEventIQString1, iqResult.toXML());

    }


    @Test
    public void testJingleParsing() {

        JingleChannel channel = new JingleChannel();
        channel.setProtocol(JingleChannel.UDP);
        IQ iq = new JingleChannelIQ(channel);
        iq.setFrom("+16109862251@sipin3601.ym.ms");
        iq.setType(IQ.Type.get);
        iq.setTo("+16109862251@sjin360.ym.ms");
        iq.setID("764-35019616");

        JingleChannelIQ iq2 = JingleChannelIQ.fromXml(iq);

        System.out.println(iq2.toXML());

    }


    @Test
    public void testAchievementEvent() {

        AchievementEventIQ iq = AchievementEventIQ.createRequest(new JID("me@ut.ms"), new JID("achievement.ut.ms"), "500");

        assertTrue(iq.toXML().contains("key=\"call_killed\" value=\"500\""));

        System.out.println(iq.toXML());


    }



}
