package com.upptalk.jinglertpengine.ng.protocol;

import com.upptalk.jinglertpengine.util.Bencode;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * NG Protocol command
 *
 * @author bhlangonijr
 *         Date: 4/6/14
 *         Time: 8:50 PM
 */
public class NgCommand implements Serializable {

    private final NgCommandType ngCommandType;
    private final Map<String, Object> parameters;
    private final String cookie;

    public static final String COMMAND_ARG = "command";

    private NgCommand(NgCommandType ngCommandType, Map<String, Object> parameters, String cookie) {
        this.ngCommandType = ngCommandType;
        this.parameters = parameters;
        this.cookie = cookie;
    }

    public static class Builder {

        private NgCommandType ngCommandType;
        private Map<String, Object> parameters;
        private String cookie;

        public Builder() {
            parameters = new HashMap<String, Object>(20);
        }

        public final NgCommand build() throws IllegalArgumentException {

            if (ngCommandType == null) {
                throw new IllegalArgumentException("Property ngCommandType is required!");
            }

            if (cookie == null) {
                throw new IllegalArgumentException("Property cookie is required!");
            }

            return new NgCommand(this.ngCommandType, Collections.unmodifiableMap(this.parameters), this.cookie);
        }

        public Builder setNgCommandType(NgCommandType ngCommandType) {
            this.ngCommandType = ngCommandType;
            parameters.put(COMMAND_ARG, ngCommandType.getDescription());
            return this;
        }

        public Builder setParameter(final String key, final Object value) {
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

    public NgCommandType getNgCommandType() {
        return ngCommandType;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public String getCookie() {
        return cookie;
    }

    public String toBencode() throws Exception {
        return this.cookie + " " +Bencode.encode(this.getParameters());
    }

    public static NgCommand fromBencode(InputStream inputStream) throws Exception {

        final String cookie = readCookie(inputStream);

        final Builder builder = new Builder();
        Map map = Bencode.decode(inputStream);

        String cmd = null;

        for (Object entry: map.values()) {
            if (entry instanceof Map) {
                Map m = (Map)entry;
                cmd = (String) m.get(COMMAND_ARG);
                for (Object key: m.keySet()) {
                    Object value = m.get(key);
                    if (key instanceof String && value instanceof String) {
                        builder.setParameter((String)key, (String)value);
                    }
                }
            }
        }

        if (cmd == null) {
            throw new Exception("Couldn't find command type in the message!");
        }

        builder.setNgCommandType(NgCommandType.valueOf(cmd.replace("_", "")));
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
        return "NgCommand{" +
                "ngCommandType=" + ngCommandType +
                ", parameters=" + parameters +
                ", cookie='" + cookie + '\'' +
                '}';
    }
}
