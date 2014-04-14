package com.upptalk.jinglertpengine.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamingThreadFactory implements ThreadFactory {

    private AtomicInteger number = new AtomicInteger(0);
    private String prefix;
    private final String SEPARATOR = "-";
    final ThreadGroup group;

    public NamingThreadFactory(final String prefix) {
        if (prefix != null) {
            this.prefix = prefix;
        } else {
            this.prefix = "NoNameThread";
        }
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() :
                Thread.currentThread().getThreadGroup();
    }

    public Thread newThread(Runnable runnable) {
        number.incrementAndGet();
        final Thread t = new Thread(group, runnable, prefix + SEPARATOR + number.toString(), 0);
        if (t.isDaemon()) {
            t.setDaemon(false);
        }
        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }
        return t;
    }
}