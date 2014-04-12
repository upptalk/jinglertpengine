package com.upptalk.jinglertpengine.ng.protocol;

/**
 * @author bhlangonijr
 *         Date: 4/6/14
 *         Time: 8:43 PM
 */
public enum NgCommandType {

    ping("ping"),
    offer("offer"),
    answer("answer"),
    delete("delete"),
    query("query"),
    startrecording("start_recording");

    private final String description;

    NgCommandType(String description) {
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
