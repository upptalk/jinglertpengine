package com.upptalk.jinglertpengine.util;

import java.util.Random;

/**
 * Random string generator
 *
 * @author bhlangonijr
 *         Date: 4/8/14
 *         Time: 11:24 PM
 */
public class RandomString {
    public static final int COOKIE_LENGTH = 7;
    private static final char[] symbols = new char[36];

    static {
        for (int idx = 0; idx < 10; ++idx) {
            symbols[idx] = (char) ('0' + idx);
        }
        for (int idx = 10; idx < 36; ++idx) {
            symbols[idx] = (char) ('a' + idx - 10);
        }
    }

    private static final Random random = new Random();

    public RandomString(int length) {
        if (length < 1) {
            throw new IllegalArgumentException("length < 1: " + length);
        }

    }

    public static char[] nextRandom(int length) {
        if (length < 1) {
            throw new IllegalArgumentException("length < 1: " + length);
        }
        final char[] buf = new char[length];
        for (int idx = 0; idx < buf.length; ++idx) {
            buf[idx] = symbols[random.nextInt(symbols.length)];
        }
        return buf;
    }

    /**
     * Generate a new Cookie random string
     * @return
     */
    public static String getCookie() {
        return new String(nextRandom(COOKIE_LENGTH));
    }

}