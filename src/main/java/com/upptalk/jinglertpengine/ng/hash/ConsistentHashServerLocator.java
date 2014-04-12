package com.upptalk.jinglertpengine.ng.hash;

import com.google.common.hash.Hashing;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.List;

/**
 * This implementation uses a consistent hash function for
 * mapping keys to available servers, which means there is no re-mapping when one server is removed
 * from the list
 *
 * @author bhlangonijr
 *         Date: 4/12/14
 *         Time: 5:11 PM
 */
public class ConsistentHashServerLocator implements ServerLocator {

    @Override
    public InetSocketAddress selectServer(String key, List<InetSocketAddress> servers) {
        int index = Hashing.consistentHash(Hashing.md5().hashString(key, Charset.defaultCharset()),
                servers.size());
        return servers.get(index);
    }
}
