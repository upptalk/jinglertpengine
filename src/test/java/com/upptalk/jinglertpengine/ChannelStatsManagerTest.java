package com.upptalk.jinglertpengine;

import com.upptalk.jinglertpengine.ng.ChannelStatsManager;
import com.upptalk.jinglertpengine.ng.NgClient;
import com.upptalk.jinglertpengine.ng.NgCommandListener;
import com.upptalk.jinglertpengine.ng.NgResultListener;
import com.upptalk.jinglertpengine.ng.protocol.NgCommand;
import com.upptalk.jinglertpengine.ng.protocol.NgResult;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

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

            ChannelStatsManager manager = new ChannelStatsManager(5000, 10);
            manager.setChannelTimeout(10000);
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
                    System.out.println("Receiving message: " + result);
                }
            });

            client.setResultListeners(listeners);
            client.setCommandListeners(commandListeners);

            Thread.sleep(60000);

            System.out.println("Closing connection");
            manager.close();
            client.close();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}
