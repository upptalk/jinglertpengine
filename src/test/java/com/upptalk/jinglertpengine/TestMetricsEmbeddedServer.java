package com.upptalk.jinglertpengine;

import com.codahale.metrics.Meter;
import com.codahale.metrics.servlets.HealthCheckServlet;
import com.codahale.metrics.servlets.MetricsServlet;
import com.upptalk.jinglertpengine.metrics.MetricsHolder;
import com.upptalk.jinglertpengine.web.EmbeddedHttpServer;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServlet;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

/**
 * @author bhlangonijr
 *         Date: 4/27/14
 *         Time: 11:39 AM
 */
public class TestMetricsEmbeddedServer {


    @org.junit.Test
    public void testMetrics() throws Exception {

        MetricsServlet servlet = new MetricsServlet();
        HealthCheckServlet servlet1 = new HealthCheckServlet();

        Map<String, HttpServlet> servletMap = new HashMap<String, HttpServlet>();
        servletMap.put("/metrics", servlet);
        servletMap.put("/healthcheck", servlet1);
        EmbeddedHttpServer server = new EmbeddedHttpServer(8081, servletMap);
        server.start();
        Thread.sleep(500);

        Meter meter = MetricsHolder.getMetrics().meter("test");

        meter.mark(100);

        URL url = new URL("http://localhost:8081/metrics");
        String result =  IOUtils.toString(url.openStream(), "UTF-8");

        System.out.println(result);

        assertTrue(result.contains("\"meters\":{\"test\":{\"count\":100"));

    }


}
