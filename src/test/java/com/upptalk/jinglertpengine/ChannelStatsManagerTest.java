package com.upptalk.jinglertpengine;

import com.upptalk.jinglertpengine.ng.NgClient;
import com.upptalk.jinglertpengine.ng.NgCommandListener;
import com.upptalk.jinglertpengine.ng.NgResultListener;
import com.upptalk.jinglertpengine.ng.protocol.NgCommand;
import com.upptalk.jinglertpengine.ng.protocol.NgResult;

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
public class ChannelStatsManagerTest {

    public static void main(String[] args) {


        try {

            NgClient client = new NgClient();
            client.setServers(new InetSocketAddress("localhost", 2223));

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

            Thread.sleep(60000);

            System.out.println("Closing connection");
            client.close();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}
