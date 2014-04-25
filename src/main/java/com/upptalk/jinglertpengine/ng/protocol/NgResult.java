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


    public final static String ERROR_REASON_UNKNOW_CALLID = "Unknown call-id";

    private final NgResultType ngResultType;
    private final Map<String, String> parameters;
    private final String cookie;
    private final Sdp sdp;
    private final String errorReason;

    public static final String RESULT_ARG = "result";
    public static final String SDP_ARG = "sdp";
    public static final String ERROR_REASON_ARG = "error-reason";

    private NgResult(NgResultType ngResultType,
                     Map<String, String> parameters,
                     String cookie, Sdp sdp, String errorReason) {
        this.ngResultType = ngResultType;
        this.parameters = parameters;
        this.cookie = cookie;
        this.sdp = sdp;
        this.errorReason = errorReason;
    }

    public static class Builder {

        private NgResultType ngResultType;
        private Map<String, String> parameters;
        private String cookie;
        private Sdp sdp;
        private String errorReason;

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

            return new NgResult(this.ngResultType, Collections.unmodifiableMap(this.parameters),
                    this.cookie, this.sdp, this.errorReason);
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

        public Builder setSdp(Sdp sdp) {
            this.sdp = sdp;
            return this;
        }

        public Builder setErrorReason(String errorReason) {
            this.errorReason = errorReason;
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

    public Sdp getSdp() {
        return sdp;
    }

    public String getErrorReason() {
        return errorReason;
    }

    public String toBencode() throws Exception {
        return this.cookie + " " +Bencode.encode(this.getParameters());
    }

    public static NgResult fromBencode(InputStream inputStream) throws Exception {

        final String cookie = readCookie(inputStream);

        final Builder builder = new Builder();
        Map map = Bencode.decode(inputStream);

        String cmd = null;
        String sdp = null;
        String errorReason = null;

        for (Object entry: map.values()) {
            if (entry instanceof Map) {
                Map m = (Map)entry;
                if (m.containsKey(RESULT_ARG)) {
                    cmd = (String) m.get(RESULT_ARG);
                }
                if (m.containsKey(SDP_ARG)) {
                    sdp = (String) m.get(SDP_ARG);
                }
                if (m.containsKey(ERROR_REASON_ARG)) {
                    errorReason = (String) m.get(ERROR_REASON_ARG);
                }
                for (Object key: m.keySet()) {
                    Object value = m.get(key);
                    if (key instanceof String && value instanceof String) {
                        builder.setParameter((String)key, (String)value);
                    }
                }

            }
        }

        if (cmd == null) {
            throw new Exception("Couldn't find response type in the message!");
        }

        builder.setNgResultType(NgResultType.valueOf(cmd.replace("_", "")));
        builder.setCookie(cookie);
        if (sdp != null) {
            builder.setSdp(Sdp.fromSdp(sdp));
        }
        if (errorReason != null) {
            builder.setErrorReason(errorReason);
        }

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
                ", sdp=" + sdp +
                ", errorReason='" + errorReason + '\'' +
                '}';
    }
}
