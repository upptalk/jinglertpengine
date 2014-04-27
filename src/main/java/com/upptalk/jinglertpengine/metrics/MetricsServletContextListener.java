package com.upptalk.jinglertpengine.metrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.servlets.HealthCheckServlet;
import com.codahale.metrics.servlets.MetricsServlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author bhlangonijr
 *         Date: 11/13/13
 *         Time: 12:41 AM
 */
public class MetricsServletContextListener implements ServletContextListener {

    static final MetricRegistry metricRegistry = MetricsHolder.getMetrics();
    static final HealthCheckRegistry healthCheckRegistry = MetricsHolder.getHealthCheck();

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        servletContextEvent.getServletContext().setAttribute(HealthCheckServlet.HEALTH_CHECK_REGISTRY,healthCheckRegistry);
        servletContextEvent.getServletContext().setAttribute(MetricsServlet.METRICS_REGISTRY, metricRegistry);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }

}