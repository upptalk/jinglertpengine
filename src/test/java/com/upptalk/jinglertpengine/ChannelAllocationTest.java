package com.upptalk.jinglertpengine;

import com.upptalk.jinglertpengine.ng.NgClient;
import com.upptalk.jinglertpengine.ng.NgCommandListener;
import com.upptalk.jinglertpengine.ng.NgResultListener;
import com.upptalk.jinglertpengine.ng.protocol.NgCommand;
import com.upptalk.jinglertpengine.ng.protocol.NgResult;
import com.upptalk.jinglertpengine.xmpp.jinglenodes.JingleChannel;
import com.upptalk.jinglertpengine.xmpp.processor.JingleChannelEventProcessor;
import com.upptalk.jinglertpengine.xmpp.processor.JingleChannelProcessor;
import com.upptalk.jinglertpengine.xmpp.processor.JingleChannelSessionManager;
import com.upptalk.jinglertpengine.xmpp.tinder.JingleChannelIQ;
import org.xmpp.packet.IQ;

import java.net.InetSocketAddress;

/**
 * Channel stats manager test
 *
 * Test required rtpengine installed
 *
 * @author bhlangonijr
 *         Date: 4/9/14
 *         Time: 11:40 PM
 */
public class ChannelAllocationTest {

    public static void main(String[] args) {

        try {

            NgClient client = new NgClient(10000, 10000, 10);
            client.setServers(new InetSocketAddress("localhost", 2223));

            ExternalComponentMock mock = new ExternalComponentMock("mock",
                    "mock", "localhost");

            client.getCommandListeners().add(new NgCommandListener() {
                @Override
                public void sent(NgCommand command, InetSocketAddress server) {
                    System.out.println("Sending message: " + command + " - " + server);
                }
            });

            client.getResultListeners().add(new NgResultListener() {
                @Override
                public void receive(NgResult result) {
                    System.out.println("Receiving message: " + result);
                }
            });

            JingleChannelProcessor processor = new JingleChannelProcessor(mock);
            JingleChannelEventProcessor eventProcessor = new JingleChannelEventProcessor(mock);
            JingleChannelSessionManager manager = new JingleChannelSessionManager(eventProcessor, processor, client);
            manager.setChannelKeepAliveTaskDelay(30000);
            processor.setSessionManager(manager);

            JingleChannelIQ request = createFakeChannelRequest("335676746_24130402@188.67.173.24",
                    "alice@188.67.173.24", "alice@188.67.173.23");

            processor.processIQ(request);

            System.out.println("RESULT IQ = " + mock.getResults().take());

            System.out.println("Closing connection");
            client.close();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static JingleChannelIQ createFakeChannelRequest(String id, String from, String to) {

        JingleChannel channel = new JingleChannel();
        channel.setProtocol(JingleChannel.UDP);
        JingleChannelIQ iq = new JingleChannelIQ(channel);
        iq.setFrom(from);
        iq.setType(IQ.Type.get);
        iq.setTo(to);
        iq.setID(id);

        return iq;
    }

}
