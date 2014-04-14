package com.upptalk.jinglertpengine.xmpp.component;

/**
 * Result Receiver
 * @author bhlangonijr
 *
 */
public interface ResultReceiver {

    public void receivedResult(IqRequest IqRequest);

    public void receivedError(IqRequest IqRequest);

    public void timeoutRequest(IqRequest IqRequest);

}
