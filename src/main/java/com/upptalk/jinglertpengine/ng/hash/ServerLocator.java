package com.upptalk.jinglertpengine.ng.hash;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * Server Locator provides an interface for implementing strategies for load balancing
 * between available servers
 *
 * @author bhlangonijr
 *         Date: 4/12/14
 *         Time: 5:03 PM
 */
public interface ServerLocator {
    /**
     * Select a server based on a key
     *
     * @param key Is the relevant key which will be used to map the requests to a given server
     * @param servers list of available servers
     * @return The server mapped to the given key
     */
    InetSocketAddress selectServer(String key, List<InetSocketAddress> servers);
}
