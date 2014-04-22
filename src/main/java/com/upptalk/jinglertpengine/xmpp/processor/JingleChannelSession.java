package com.upptalk.jinglertpengine.xmpp.processor;

import com.upptalk.jinglertpengine.ng.protocol.NgCommand;
import com.upptalk.jinglertpengine.ng.protocol.NgResult;
import com.upptalk.jinglertpengine.xmpp.tinder.JingleChannelIQ;

import java.io.Serializable;
import java.util.concurrent.ScheduledFuture;

/**
 * JingleChannel session
 *
 * @author bhlangonijr
 *         Date: 5/16/13
 *         Time: 3:09 PM
 */
public class JingleChannelSession implements Serializable {

    private final String id;
    private final JingleChannelIQ requestIQ;
    private JingleChannelIQ responseIQ;
    private NgCommand offerRequest;
    private NgResult offerResult;
    private NgCommand answerRequest;
    private NgResult answerResult;
    private ScheduledFuture keepAliveTaskFuture;

    private final long timestamp = System.currentTimeMillis();

    public JingleChannelSession(String id, JingleChannelIQ requestIQ) {
        this.id = id;
        this.requestIQ = requestIQ;
    }

    public String getId() {
        return id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public JingleChannelIQ getRequestIQ() {
        return requestIQ;
    }

    public JingleChannelIQ getResponseIQ() {
        return responseIQ;
    }

    public void setResponseIQ(JingleChannelIQ responseIQ) {
        this.responseIQ = responseIQ;
    }

    public NgCommand getOfferRequest() {
        return offerRequest;
    }

    public void setOfferRequest(NgCommand offerRequest) {
        this.offerRequest = offerRequest;
    }

    public NgResult getOfferResult() {
        return offerResult;
    }

    public void setOfferResult(NgResult offerResult) {
        this.offerResult = offerResult;
    }

    public NgCommand getAnswerRequest() {
        return answerRequest;
    }

    public void setAnswerRequest(NgCommand answerRequest) {
        this.answerRequest = answerRequest;
    }

    public NgResult getAnswerResult() {
        return answerResult;
    }

    public void setAnswerResult(NgResult answerResult) {
        this.answerResult = answerResult;
    }

    public ScheduledFuture getKeepAliveTaskFuture() {
        return keepAliveTaskFuture;
    }

    public void setKeepAliveTaskFuture(ScheduledFuture keepAliveTaskFuture) {
        this.keepAliveTaskFuture = keepAliveTaskFuture;
    }
}
