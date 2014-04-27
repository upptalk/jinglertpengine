package com.upptalk.jinglertpengine.metrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.json.MetricsModule;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.upptalk.jinglertpengine.util.NamingThreadFactory;
import org.apache.log4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Static metrics holder
 *
 * @author bhlangonijr
 *         Date: 4/27/14
 *         Time: 9:42 AM
 */
public class MetricsHolder {


    private static final Logger metricsLog = Logger.getLogger("jinglertpengine-stats");

    private static final long METRICS_LOG_SCHEDULER_DELAY = 60000;
    private static final MetricRegistry metrics = new MetricRegistry();
    private static final HealthCheckRegistry healthCheck =  new HealthCheckRegistry();
    private static final ScheduledExecutorService logService =
            Executors.newSingleThreadScheduledExecutor(new NamingThreadFactory("metrics-log-scheduler"));

    private static boolean started = false;

    /**
     * Starting logging metrics information
     */
    public static void start() {

        if (started) {
            metricsLog.info("Metrics service already started.");
            return;
        }

        metricsLog.info("Starting metrics service...");
        metrics.addListener(new JingleRtpEngineMetricsRegistry());
        final ObjectMapper jsonMapper = new ObjectMapper().registerModule(
                new MetricsModule(TimeUnit.SECONDS, TimeUnit.MILLISECONDS, false));
        jsonMapper.enable(SerializationFeature.INDENT_OUTPUT);

        logService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    metricsLog.info("STATS LOG");
                    metricsLog.info(jsonMapper.writeValueAsString(getMetrics()));
                } catch (JsonProcessingException e) {
                    metricsLog.error("Error writing stats to log file: ", e);
                }
            }
        }, METRICS_LOG_SCHEDULER_DELAY, METRICS_LOG_SCHEDULER_DELAY, TimeUnit.MILLISECONDS);

    }

    public static void stop() {
        logService.shutdownNow();
    }

    public static MetricRegistry getMetrics() {
        return metrics;
    }

    public static HealthCheckRegistry getHealthCheck() {
        return healthCheck;
    }
}
