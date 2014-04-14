package com.upptalk.jinglertpengine.metrics;

import com.codahale.metrics.*;
import org.apache.log4j.Logger;

/**
 * @author bhlangonijr
 *         Date: 11/10/13
 *         Time: 10:25 PM
 */
public class JingleRtpEngineMetricsRegistry implements MetricRegistryListener {

    private final static Logger log = Logger.getLogger(JingleRtpEngineMetricsRegistry.class);

    @Override
    public void onGaugeAdded(String s, Gauge<?> gauge) {
        log.debug("Added: " + gauge.toString());
    }

    @Override
    public void onGaugeRemoved(String s) {
        log.debug("Removed: " + s);
    }

    @Override
    public void onCounterAdded(String s, Counter counter) {
        log.debug("Added: " + counter.toString());
    }

    @Override
    public void onCounterRemoved(String s) {
        log.debug("Removed: " + s);
    }

    @Override
    public void onHistogramAdded(String s, Histogram histogram) {
        log.debug("Added: " + histogram.toString());
    }

    @Override
    public void onHistogramRemoved(String s) {
        log.debug("Removed: " + s);
    }

    @Override
    public void onMeterAdded(String s, Meter meter) {
        log.debug("Added: " + meter.toString());
    }

    @Override
    public void onMeterRemoved(String s) {
        log.debug("Removed: " + s);
    }

    @Override
    public void onTimerAdded(String s, Timer timer) {
        log.debug("Added: " + timer.toString());
    }

    @Override
    public void onTimerRemoved(String s) {
        log.debug("Removed: " + s);
    }
}
