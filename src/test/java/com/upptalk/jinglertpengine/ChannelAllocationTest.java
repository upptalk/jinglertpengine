package com.upptalk.jinglertpengine;

import com.upptalk.jinglertpengine.ng.ChannelStatsManager;
import com.upptalk.jinglertpengine.ng.NgClient;
import com.upptalk.jinglertpengine.ng.NgCommandListener;
import com.upptalk.jinglertpengine.ng.NgResultListener;
import com.upptalk.jinglertpengine.ng.protocol.NgCommand;
import com.upptalk.jinglertpengine.ng.protocol.NgResult;
import com.upptalk.jinglertpengine.xmpp.jinglenodes.JingleChannel;
import com.upptalk.jinglertpengine.xmpp.processor.JingleChannelProcessor;
import com.upptalk.jinglertpengine.xmpp.processor.JingleChannelSessionManager;
import com.upptalk.jinglertpengine.xmpp.tinder.JingleChannelIQ;
import org.eclipse.jetty.util.BlockingArrayQueue;
import org.xmpp.packet.IQ;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

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

        final BlockingQueue<NgResult> results = new BlockingArrayQueue<>(10);

        try {

            NgClient client = new NgClient();
            client.setServers(new InetSocketAddress("localhost", 2223));

            ChannelStatsManager manager = new ChannelStatsManager(15000, 10);
            manager.setChannelTimeout(30000);
            manager.setNgClient(client);

            List<NgCommandListener> commandListeners = new ArrayList<>();
            commandListeners.add(manager);
            commandListeners.add(new NgCommandListener() {
                @Override
                public void sent(NgCommand command, InetSocketAddress server) {
                    System.out.println("Sending message: " + command + " - " + server);
                }
            });

            List<NgResultListener> listeners = new ArrayList<>();
            listeners.add(manager);
            listeners.add(new NgResultListener() {
                @Override
                public void receive(NgResult result) {
                    results.offer(result);
                    System.out.println("Receiving message: " + result);
                }
            });

            client.setResultListeners(listeners);
            client.setCommandListeners(commandListeners);

            JingleChannelSessionManager sessionManager = new JingleChannelSessionManager(client);
            JingleChannelProcessor processor = new JingleChannelProcessor(sessionManager);

            JingleChannelIQ request = createFakeChannelRequest("335676746_24130402@188.67.173.24",
                    "alice@188.67.173.24", "alice@188.67.173.23");

            processor.processIQ(request);

            System.out.println("Result: " + results.take());

            System.out.println("Closing connection");
            manager.close();
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
