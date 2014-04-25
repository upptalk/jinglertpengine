package com.upptalk.jinglertpengine;

import com.upptalk.jinglertpengine.ng.NgClient;
import com.upptalk.jinglertpengine.ng.NgCommandListener;
import com.upptalk.jinglertpengine.ng.NgResultListener;
import com.upptalk.jinglertpengine.ng.protocol.NgCommand;
import com.upptalk.jinglertpengine.ng.protocol.NgResult;
import com.upptalk.jinglertpengine.util.RandomString;
import com.upptalk.jinglertpengine.web.EmbeddedHttpServer;
import com.upptalk.jinglertpengine.xmpp.jinglenodes.JingleChannel;
import com.upptalk.jinglertpengine.xmpp.processor.JingleChannelProcessor;
import com.upptalk.jinglertpengine.xmpp.tinder.JingleChannelIQ;
import org.xmpp.packet.IQ;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Channel stats manager test
 *
 * Test required rtpengine installed
 *
 * @author bhlangonijr
 *         Date: 4/9/14
 *         Time: 11:40 PM
 */
public class ChannelAllocationAndQueryTest {

    static final String play = "/opt/yuilop/pjsip1/pjsip-apps/bin/samples/x86_64-unknown-linux-gnu/streamutil " +
            "--send-recv --codec=PCMA --remote=${remote_host}:${remote_port} --local-port=${local_port} " +
            "--record-file=/opt/yuilop/pjsip1/pjsip-apps/bin/samples/x86_64-unknown-linux-gnu/${file}.wav " +
            "--play-file=/opt/yuilop/pjsip1/pjsip-apps/bin/samples/x86_64-unknown-linux-gnu/voicemail1as.wav";

    static final ExecutorService service = Executors.newFixedThreadPool(4);
    static EmbeddedHttpServer server;
    static int port = 8080;


    public static void main(String[] args) {

        try {
            server = new EmbeddedHttpServer(port);
            server.start();
            NgClient client = new NgClient(40000, 50000, 10);
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

            JingleChannelProcessor processor = new JingleChannelProcessor(mock, client);
            processor.setChannelKeepAliveTaskDelay(30000);

            JingleChannelIQ request = createFakeChannelRequest(RandomString.getCookie()+"_24130402@127.0.0.1",
                    "alice@127.0.0.1", "bob@127.0.0.1");

            processor.processIQ(request);

            JingleChannelIQ channelIQ = (JingleChannelIQ) mock.getResults().take();

            System.out.println("Allocation IQ = " + channelIQ);

            playStream(channelIQ.getJingleChannel().getHost(),
                    "6025", channelIQ.getJingleChannel().getRemoteport()+"");


            playStream(channelIQ.getJingleChannel().getHost(),
                    "6027", channelIQ.getJingleChannel().getLocalport()+"");

            Thread.sleep(300000);

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

    private static void playStream(final String host, final String localPort, final String remotePort) {

        service.execute(new Runnable() {
            @Override
            public void run() {
                long init = System.currentTimeMillis();
                MediaProcess p = new MediaProcess("play", play.replace("${local_port}", localPort).
                        replace("${remote_port}", remotePort).replace("${remote_host}", host).
                        replace("${file}", "a"+ RandomString.nextRandom(10)));

                while (true) {
                    String line = null;
                    try {
                        line = p.readLine();
                        if (!p.isStarted()) {
                            break;
                        }
                        if (System.currentTimeMillis() - init > 15000) {
                            p.destroy();
                        }

                        if (line==null || line.trim().equals("")) {
                            continue;
                        }
                        if (!line.contains(" GET prefetch")) {
                          //  System.out.println("[MEDIA ENGINE]: " + line);
                        }
                    } catch (Exception e) {
                        if (e instanceof IOException || e instanceof NullPointerException ) {
                            e.printStackTrace();
                            break;
                        } else {
                            e.printStackTrace();
                        }
                    }
                }

            }
        });

    }

}
