package com.upptalk.jinglertpengine.ng.protocol;

/**
 * NG result types
 * @author bhlangonijr
 *         Date: 4/6/14
 *         Time: 8:43 PM
 */
public enum NgResultType {

    pong("pong"),
    ok("ok"),
    error("error"),
    timeout("timeout");

    private final String description;

    NgResultType(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return this.description;
    }

    public String getDescription() {
        return description;
    }
}
