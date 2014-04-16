package com.upptalk.jinglertpengine.xmpp.processor;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.upptalk.jinglertpengine.ng.NgClient;
import com.upptalk.jinglertpengine.ng.NgResultListener;
import com.upptalk.jinglertpengine.ng.protocol.NgCommand;
import com.upptalk.jinglertpengine.ng.protocol.NgCommandType;
import com.upptalk.jinglertpengine.ng.protocol.NgResult;
import com.upptalk.jinglertpengine.util.RandomString;
import com.upptalk.jinglertpengine.xmpp.tinder.JingleChannelIQ;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;

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
    private final NgClient ngClient;

    private ConcurrentLinkedHashMap<String, JingleChannelSession> sessions = new ConcurrentLinkedHashMap.Builder().
            maximumWeightedCapacity(SESSION_MAX_ENTRIES).build();

    public JingleChannelSessionManager(NgClient ngClient) {
        Assert.notNull(ngClient);
        this.ngClient = ngClient;
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

        JingleChannelSession s = JingleChannelSession.create(id, requestIQ);
        sessions.put(id, s);

        final String cookie = RandomString.getCookie();

        final NgCommand offer = NgCommand.builder().
                setCookie(cookie).
                setNgCommandType(NgCommandType.offer).
                //setParameter().
                build();


        ngClient.send(offer, requestIQ.getFrom().getNode());

        return s;
    }



    public JingleChannelSession getSession(String id) throws JingleChannelException {

        final JingleChannelSession s = sessions.get(id);

        if (s == null) {
            throw new JingleChannelException("Couldn't retrive Jingle Channel session with id: " + id);
        }

        return s;
    };

    @Override
    public void receive(NgResult result) {

    }

    public NgClient getNgClient() {
        return ngClient;
    }
}
