package com.upptalk.jinglertpengine.ng;

import com.upptalk.jinglertpengine.ng.protocol.NgResult;

/**
 * Media proxy ng result listener
 *
 * @author bhlangonijr
 *         Date: 4/8/14
 *         Time: 11:32 PM
 */
public interface NgResultListener {

    /**
     * Listen to the result messages from Ng server
     *
     * @param result
     */
    void receive(NgResult result);
}
