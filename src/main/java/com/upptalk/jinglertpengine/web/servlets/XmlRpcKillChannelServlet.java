package com.upptalk.jinglertpengine.web.servlets;

import com.upptalk.jinglertpengine.xmpp.processor.JingleChannelSessionManager;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * This service will be responding to xmlrpc callbacks
 * from mediaproxy to notify that a channel has been killed
 *
 * @author bhlangonijr
 *         Date: 4/27/14
 *         Time: 9:11 AM
 */

@SuppressWarnings("serial")
public class XmlRpcKillChannelServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger(XmlRpcKillChannelServlet.class);

    private final static String responseOK = "<methodResponse><params>" +
            "<param><value><string>ok</string></value></param>" +
            "</params></methodResponse>";

    private final static String responseFault = "<methodResponse>" +
            "<fault><string>error</string></fault>" +
            "</methodResponse>";

    private final JingleChannelSessionManager sessionManager;

    public XmlRpcKillChannelServlet(final JingleChannelSessionManager sessionManager) {
        Assert.notNull(sessionManager);
        this.sessionManager = sessionManager;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (log.isDebugEnabled()) {
            log.debug("HTTP GET: " + request.getRemoteAddr());
        }

        handleResponse(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        if (log.isDebugEnabled()) {
            log.debug("HTTP POST: " + request.getRemoteAddr());
        }

        handleResponse(request, response);
    }


    protected void handleResponse(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        response.setContentType("text/xml");

        try {

            final XmlRpcRequest req = XmlRpcRequest.parseFromStream(request.getInputStream());
            if (log.isDebugEnabled()) {
                log.debug("Request parsed: " + req);
            }

            sessionManager.notifyAndRemoveChannelSession(req.getParams().get(2));

        } catch(Exception e) {
            log.error("Error while parsing request from media proxy ", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println(responseOK);
            return;
        }
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println(responseOK);

    }

    private static String getBody(HttpServletRequest request) throws IOException {

        String body = null;
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            } else {
                stringBuilder.append("");
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    throw ex;
                }
            }
        }

        body = stringBuilder.toString();
        return body;
    }

    public JingleChannelSessionManager getSessionManager() {
        return sessionManager;
    }
}

