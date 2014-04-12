package com.upptalk.jinglertpengine.ng.protocol;

import com.upptalk.jinglertpengine.util.Bencode;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * NG Protocol result message
 *
 * @author bhlangonijr
 *         Date: 4/6/14
 *         Time: 8:50 PM
 */
public class NgResult implements Serializable {

    private final NgResultType ngResultType;
    private final Map<String, String> parameters;
    private final String cookie;

    public static final String RESULT_ARG = "result";

    private NgResult(NgResultType ngResultType, Map<String, String> parameters, String cookie) {
        this.ngResultType = ngResultType;
        this.parameters = parameters;
        this.cookie = cookie;
    }

    public static class Builder {

        private NgResultType ngResultType;
        private Map<String, String> parameters;
        private String cookie;

        public Builder() {
            parameters = new HashMap<String, String>(20);
        }

        public final NgResult build() throws IllegalArgumentException {

            if (ngResultType == null) {
                throw new IllegalArgumentException("Property ngResultType is required!");
            }

            if (cookie == null) {
                throw new IllegalArgumentException("Property cookie is required!");
            }

            return new NgResult(this.ngResultType, Collections.unmodifiableMap(this.parameters), this.cookie);
        }

        public Builder setNgResultType(NgResultType ngResultType) {
            this.ngResultType = ngResultType;
            parameters.put(RESULT_ARG, ngResultType.getDescription());
            return this;
        }

        public Builder setParameter(final String key, final String value) {
            parameters.put(key, value);
            return this;
        }

        public Builder setCookie(String cookie) {
            this.cookie = cookie;
            return this;
        }

    }

    public static final Builder builder() {
        return new Builder();
    }

    public NgResultType getNgResultType() {
        return ngResultType;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public String getCookie() {
        return cookie;
    }

    public String toBencode() throws Exception {
        return this.cookie + " " +Bencode.encode(this.getParameters());
    }

    public static NgResult fromBencode(InputStream inputStream) throws Exception {

        final String cookie = readCookie(inputStream);

        final Builder builder = new Builder();
        Map map = Bencode.decode(inputStream);

        String cmd = null;

        for (Object entry: map.values()) {
            Map m = (Map)entry;
            cmd = (String) m.get(RESULT_ARG);
            if (cmd != null) {
                break;
            }
        }

        if (cmd == null) {
            throw new Exception("Couldn't find response type in the message!");
        }

        builder.setNgResultType(NgResultType.valueOf(cmd.replace("_", "")));
        builder.setCookie(cookie);

        return builder.build();
    }

    /**
     * Read the cookie part of from NG command/response and
     * @param is
     * @return
     * @throws Exception
     */
    private static String readCookie(InputStream is) throws Exception {
        StringBuilder sb = new StringBuilder();
        int readByte;
        char charByte;
        int index = 0;
        try {
            readByte = is.read();
            charByte = (char) readByte;
            while(readByte != -1 && charByte != ' ') {
                sb.append(charByte);
                readByte = is.read();
                charByte = (char) readByte;
            }
        }
        catch(Exception ex) {
            throw new Exception("Errro reading Cookie", ex);
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "NgResult{" +
                "ngResultType=" + ngResultType +
                ", parameters=" + parameters +
                ", cookie='" + cookie + '\'' +
                '}';
    }
}
