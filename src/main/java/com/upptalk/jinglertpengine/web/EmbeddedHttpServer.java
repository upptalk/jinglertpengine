package com.upptalk.jinglertpengine.web;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Embedded HTTP server
 *
 * @author bhlangonijr
 *         Date: 3/28/14
 *         Time: 5:06 PM
 */
public class EmbeddedHttpServer {
    final static Logger log = Logger.getLogger(EmbeddedHttpServer.class);
    private Server server;
    private final int port;

    public EmbeddedHttpServer(int port) {
        this.port = port;
    }

    public EmbeddedHttpServer() {
        this(8080);
    }

    public void start() throws Exception {
        server = new Server(port);
        ServletHandler handler = new ServletHandler();
        server.setHandler(handler);
        handler.addServletWithMapping(new ServletHolder(new XmlRpcServlet()), "/xmlrpc");
        server.start();
        //server.join();
    }

    public void stop() throws Exception {
        if (server != null) {
            server.stop();
        }
    }

    @SuppressWarnings("serial")
    public static class XmlRpcServlet extends HttpServlet {

        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response)
                throws ServletException, IOException {
            response.setContentType("text/html");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println("<h1>Hello SimpleServlet</h1>");
        }

        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            System.out.println("HTTP POST: " + getBody(req));
            resp.setContentType("text/xml");
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().println("<methodResponse><params>" +
                    "<param><value><string>ok</string></value></param>" +
                    "</params></methodResponse>");
        }
    }


    public static String getBody(HttpServletRequest request) throws IOException {

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
}
