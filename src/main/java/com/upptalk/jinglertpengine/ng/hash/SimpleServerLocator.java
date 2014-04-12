package com.upptalk.jinglertpengine.ng.hash;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * Simple strategy for load balancing the requests to the available servers
 *
 * @author bhlangonijr
 *         Date: 4/12/14
 *         Time: 5:11 PM
 */
public class SimpleServerLocator implements ServerLocator {

    @Override
    public InetSocketAddress selectServer(String key, List<InetSocketAddress> servers) {
        return servers.get(key.hashCode() % servers.size());
    }
}
