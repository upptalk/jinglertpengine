package com.upptalk.jinglertpengine;

import com.upptalk.jinglertpengine.ng.NgClient;
import com.upptalk.jinglertpengine.ng.NgResultListener;
import com.upptalk.jinglertpengine.ng.protocol.NgCommand;
import com.upptalk.jinglertpengine.ng.protocol.NgCommandType;
import com.upptalk.jinglertpengine.ng.protocol.NgResult;
import org.eclipse.jetty.util.BlockingArrayQueue;

import java.net.InetSocketAddress;
import java.util.concurrent.BlockingQueue;

/**
 * Media proxy client test
 *
 * @author bhlangonijr
 *         Date: 4/9/14
 *         Time: 11:40 PM
 */
public class NgClientTest {

    public static void main(String[] args) {

        final int N = 5;
        final BlockingQueue<NgResult> queue = new BlockingArrayQueue<NgResult>(10);

        try {

            NgClient client = new NgClient();
            client.setServers(new InetSocketAddress("localhost", 2223));
            client.getResultListeners().add(new NgResultListener() {
                @Override
                public void receive(NgResult result) {
                    queue.offer(result);
                }
            });
            long init = System.currentTimeMillis();

            for (int i=1; i<=N; i++) {
                NgCommand ping = NgCommand.builder()
                        .setCookie("5323_"+i)
                        .setNgCommandType(NgCommandType.ping)
                        .build();

                client.send(ping, "me");
                System.out.println((System.currentTimeMillis() - init) + " :: Sent message: " + ping);
            }

            for (int i=1; i<=N ;i++) {
                System.out.println((System.currentTimeMillis() - init) + " :: Received message: " + queue.take());
            }

            System.out.println("Closing connection");
            client.close();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}
