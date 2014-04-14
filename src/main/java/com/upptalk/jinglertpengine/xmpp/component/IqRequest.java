package com.upptalk.jinglertpengine.xmpp.component;

import org.xmpp.packet.IQ;

/**
 * IQ Request
 * @author bhlangonijr
 *
 */
public class IqRequest {
    private int tries;
    private final Object originalPacket;
    private final IQ request;
    private IQ result;
    private final ResultReceiver resultReceiver;

    public IqRequest(Object originalPacket, IQ request, ResultReceiver resultReceiver) {
        this.originalPacket = originalPacket;
        this.request = request;
        this.resultReceiver = resultReceiver;
    }

    public int getTries() {
        return tries;
    }

    public Object getOriginalPacket() {
        return originalPacket;
    }

    public IQ getRequest() {
        return request;
    }

    public IQ getResult() {
        return result;
    }

    public void setResult(IQ result) {
        this.result = result;
    }

    public ResultReceiver getResultReceiver() {
        return resultReceiver;
    }

    public void incTries() {
        this.tries++;
    }

}
