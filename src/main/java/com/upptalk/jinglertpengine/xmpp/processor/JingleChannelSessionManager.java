package com.upptalk.jinglertpengine.xmpp.processor;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.upptalk.jinglertpengine.ng.NgClient;
import com.upptalk.jinglertpengine.ng.NgResultListener;
import com.upptalk.jinglertpengine.ng.protocol.NgCommand;
import com.upptalk.jinglertpengine.ng.protocol.NgCommandType;
import com.upptalk.jinglertpengine.ng.protocol.NgResult;
import com.upptalk.jinglertpengine.ng.protocol.NgResultType;
import com.upptalk.jinglertpengine.util.NamingThreadFactory;
import com.upptalk.jinglertpengine.util.RandomString;
import com.upptalk.jinglertpengine.util.SdpUtil;
import com.upptalk.jinglertpengine.xmpp.tinder.JingleChannelIQ;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Jingle Channel Session Manager
 *
 * @author bhlangonijr
 *         Date: 4/15/14
 *         Time: 11:04 AM
 */
public class JingleChannelSessionManager implements NgResultListener {

    final static Logger log = Logger.getLogger(JingleChannelSessionManager.class);
    private static final int SESSION_MAX_ENTRIES = 10000;
    private static final List<String> flags;

    static {
        List<String> l = new ArrayList<String>();
        l.add("trust address");
        flags = Collections.unmodifiableList(l);
    }

    private final NgClient ngClient;
    private final JingleChannelProcessor channelProcessor;

    private final ConcurrentLinkedHashMap<String, JingleChannelSession> sessions;
    private final ConcurrentLinkedHashMap<String, JingleChannelSession> sessionsByCookie;

    private final ScheduledExecutorService scheduledService;

    public JingleChannelSessionManager(final JingleChannelProcessor channelProcessor, final NgClient ngClient) {
        Assert.notNull(channelProcessor);
        Assert.notNull(ngClient);
        this.ngClient = ngClient;
        this.channelProcessor = channelProcessor;
        sessions = new ConcurrentLinkedHashMap.Builder().
                maximumWeightedCapacity(SESSION_MAX_ENTRIES).build();
        sessionsByCookie = new ConcurrentLinkedHashMap.Builder().
                maximumWeightedCapacity(SESSION_MAX_ENTRIES).build();
        scheduledService = Executors.newSingleThreadScheduledExecutor(new NamingThreadFactory("ChannelKillTask"));
        ngClient.getResultListeners().add(this);
    }

    /**
     * Creates a new Relay Channel Session
     *
     * @param id
     * @param requestIQ
     * @return the session {@link com.upptalk.jinglertpengine.xmpp.processor.JingleChannelSession}
     */
    public JingleChannelSession createSession(String id, JingleChannelIQ requestIQ) throws Exception {
        if (sessions.size() >= SESSION_MAX_ENTRIES) {
            throw new JingleChannelManagerException("Maximum number of channels exceeded " + SESSION_MAX_ENTRIES);
        }
        final JingleChannelSession s = new JingleChannelSession(id, requestIQ);
        sessions.put(s.getRequestIQ().getID(), s);
        sendOfferRequest(s);
        return s;
    }

    /**
     * Retrieve a channel session
     *
     * @param id Id given for creating the session
     *
     * @return the channel session
     * @throws JingleChannelException
     */
    public JingleChannelSession getSession(String id) throws JingleChannelException {
        final JingleChannelSession s = sessions.get(id);
        if (s == null) {
            throw new JingleChannelException("Couldn't retrive Jingle Channel session with id: " + id);
        }
        return s;
    };

    /**
     * Destroy a channel session
     *
     * @param id Id given for creating the session
     * @return the destroyed channel session
     */
    public JingleChannelSession destroySession(String id) {

        JingleChannelSession s = sessions.remove(id);

        return s;
    };

    @Override
    public void receive(NgResult result) {

        final JingleChannelSession s = sessionsByCookie.remove(result.getCookie());
        if (s != null) {
            if (s.getOfferRequest() != null && s.getOfferRequest().getCookie().equals(result.getCookie())) {
                s.setOfferResult(result);
                if (log.isDebugEnabled()) {
                    log.debug("Offer Result: " + result + " \n for command " + s.getOfferRequest());
                }
                if (result.getNgResultType().equals(NgResultType.timeout) ||
                        result.getNgResultType().equals(NgResultType.error)) {
                    getChannelProcessor().sendChannelError(s.getRequestIQ(), result.getParameters().get("result"));
                } else /*NgResultType.ok*/ {
                    try {
                        sendAnswerRequest(s);
                    } catch (Exception e) {
                        log.error("Error sending answer message", e);
                    }
                }
            } else if (s.getAnswerRequest() != null && s.getAnswerRequest().getCookie().equals(result.getCookie())) {
                s.setAnswerResult(result);
                if (log.isDebugEnabled()) {
                    log.debug("Answer Result: " + result + " \n for command " + s.getAnswerRequest());
                }
                if (result.getNgResultType().equals(NgResultType.timeout) ||
                        result.getNgResultType().equals(NgResultType.error)) {
                    getChannelProcessor().sendChannelError(s.getRequestIQ(), result.getParameters().get("result"));
                } else /*NgResultType.ok*/ {
                    try {
                        try {
                            if (log.isDebugEnabled()) {
                                log.debug("** SDP: " + s.getAnswerResult().getSdp().getConnectionAddress() + " \n " +
                                " " + s.getRequestIQ().getJingleChannel().getProtocol());
                            }

                            String host = s.getAnswerResult().getSdp().getConnectionAddress();
                            String protocol = s.getRequestIQ().getJingleChannel().getProtocol();
                            Integer locaPort = s.getOfferResult().getSdp().getMediaPort();
                            Integer remotePort = s.getAnswerResult().getSdp().getMediaPort();
                            getChannelProcessor().sendChannelResult(s.getRequestIQ(),
                                    host, protocol, locaPort, remotePort);
                        } catch (Exception e) {
                            getChannelProcessor().sendChannelError(s.getRequestIQ(),
                                    "Error extracting SDP data");
                            log.debug("Exception extracting SDP data", e);
                        }
                    } catch (Exception e) {
                        log.error("Error sending channel result", e);
                    }
                }
            } else {
                log.warn("Don't know how to handle response: " + result);
            }

        } else {
            getChannelProcessor().sendChannelError(s.getRequestIQ(), result.getParameters().get("result"));
            log.warn("Couldn't find channel session for cookie: " + result.getCookie());
        }
    }

    // Send offer with sdp
    private void sendOfferRequest(final JingleChannelSession s) throws Exception {
        final String cookie = RandomString.getCookie();
        final NgCommand offer = NgCommand.builder().
                setCookie(cookie).
                setNgCommandType(NgCommandType.offer).
                setParameter("call-id", s.getRequestIQ().getID()).
                setParameter("from-tag", SdpUtil.getFakeFromTag(s.getRequestIQ())).
                setParameter("flags", flags).
                setParameter("sdp", SdpUtil.fakeSdp.toString()).
                build();
        sessionsByCookie.put(cookie, s);
        ngClient.send(offer, s.getRequestIQ().getFrom().getNode());
        s.setOfferRequest(offer);
        if (log.isDebugEnabled()) {
            log.debug("Sent offer command: " + offer);
        }

    }

    //Send answer with sdp
    private void sendAnswerRequest(final JingleChannelSession s) throws Exception {
        final String cookie = RandomString.getCookie();
        final NgCommand answer = NgCommand.builder().
                setCookie(cookie).
                setNgCommandType(NgCommandType.answer).
                setParameter("call-id", s.getRequestIQ().getID()).
                setParameter("from-tag", SdpUtil.getFakeFromTag(s.getRequestIQ())).
                setParameter("to-tag", SdpUtil.getFakeToTag(s.getRequestIQ())).
                setParameter("flags", flags).
                setParameter("sdp", SdpUtil.fakeSdp.toString()).
                build();
        sessionsByCookie.put(cookie, s);
        ngClient.send(answer, s.getRequestIQ().getFrom().getNode());
        s.setAnswerRequest(answer);
        if (log.isDebugEnabled()) {
            log.debug("Sent answer command: " + answer);
        }
    }

    //Send channel query request
    private void sendQueryRequest(final JingleChannelSession s) throws Exception {
        final String cookie = RandomString.getCookie();
        final NgCommand query = NgCommand.builder().
                setCookie(cookie).
                setNgCommandType(NgCommandType.query).
                setParameter("call-id", s.getRequestIQ().getID()).
                build();
        sessionsByCookie.put(cookie, s);
        ngClient.send(query, s.getRequestIQ().getFrom().getNode());
        s.setAnswerRequest(query);
        if (log.isDebugEnabled()) {
            log.debug("Sent query command: " + query);
        }
    }

    static class ChannelKillTask implements Runnable {

        final JingleChannelSession session;

        ChannelKillTask(JingleChannelSession session) {
            this.session = session;
        }

        @Override
        public void run() {

        }
    }

    public NgClient getNgClient() {
        return ngClient;
    }

    public JingleChannelProcessor getChannelProcessor() {
        return channelProcessor;
    }

}
