package com.upptalk.jinglertpengine.web;

import com.upptalk.jinglertpengine.metrics.MetricsServletContextListener;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.http.HttpServlet;
import java.util.Map;

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
    private ServletContextHandler context;
    private final int port;
    private final Map<String, HttpServlet> servletMapping;

    public EmbeddedHttpServer(int port, Map<String, HttpServlet> servletMapping) {
        this.port = port;
        this.servletMapping = servletMapping;
    }

    public EmbeddedHttpServer(Map<String, HttpServlet> servletMapping) {
        this(8080, servletMapping);
    }

    public void start() throws Exception {
        log.info("Starting embedded http server on port: " + port);
        server = new Server(port);
        context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        server.setHandler(context);

        if (servletMapping != null) {
            for (Map.Entry<String, HttpServlet> entry: servletMapping.entrySet()) {
                context.addServlet(new ServletHolder(entry.getValue()), entry.getKey());
            }
        }

        context.addEventListener(new MetricsServletContextListener());

        server.start();
        //server.join();
    }

    public void stop() throws Exception {
        if (server != null) {
            log.info("Stopped embedded http server");
            server.stop();
        }
    }

}
