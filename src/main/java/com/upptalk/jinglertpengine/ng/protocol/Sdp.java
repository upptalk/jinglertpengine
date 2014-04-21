package com.upptalk.jinglertpengine.ng.protocol;

import java.io.Serializable;

/**
 * SDP Session Description Protocol
 * data class
 * TODO many SDP fields to be mapped
 * @author bhlangonijr
 *         Date: 4/18/14
 *         Time: 5:42 PM
 */
public class Sdp implements Serializable {

    private final static String LB = "\r\n";

    private String sessionName = "stream"; // s=
    private String originUsername = "root"; // o=[0]
    private String originSessionId = "123";
    private String originSessionVersion = "123";
    private String originNetType = "IN";
    private String originAddressType = "IP4"; // o=[4]
    private String originAddress; // o=[5]
    private String connectionNetType = "IN";
    private String connectionAddressType = "IP4"; // c=[1]
    private String connectionAddress; // c=[2]
    private String mediaType = "audio"; // m=[0]
    private int mediaPort; //m=[1]
    private String mediaProtocol = "RTP/AVP";
    private String sdp;

    private Sdp() {};

    /**
     * Creates de SDP object based on a string
     *
     * @param sdpString
     * @return SDP
     */
    public static Sdp fromSdp(String sdpString) {
        Sdp sdp = new Sdp();
        for (String line: sdpString.split("\n")) {
            final String field[] = line.split("=");
            if (field[0].equals("s")) {
                sdp.setSessionName(field[1]);
            } else if (field[0].equals("o")) {
                sdp.setOriginUsername(getItem(field[1], 0));
                sdp.setOriginAddressType(getItem(field[1], 4));
                sdp.setOriginAddress(getItem(field[1], 5));
            } else if (field[0].equals("c")) {
                sdp.setConnectionAddressType(getItem(field[1], 1));
                sdp.setConnectionAddress(getItem(field[1], 2));
            } else if (field[0].equals("m")) {
                sdp.setMediaType(getItem(field[1], 0));
                sdp.setMediaPort(Integer.parseInt(getItem(field[1], 1)));
            }
        }
        sdp.setSdp(sdpString);
        return sdp;
    }


    /**
     * Creates basic SDP for two audio streams using IP4 address type and RTP protocol
     *
     * @param originAddress
     * @param connectionAddress
     * @param mediaPort
     * @return
     */
    /*
    add more factory methods
     */
    public static Sdp createTwoAudioRtpIp4Basic(String originAddress, String connectionAddress, int mediaPort) {
        Sdp sdp = new Sdp();
        sdp.setOriginAddress(originAddress);
        sdp.setConnectionAddress(connectionAddress);
        sdp.setMediaPort(mediaPort);
        return sdp;
    };

    private static final String getItem(String fieldValue, int index) {
        final String[] values = fieldValue.replace("\r","").replace("\n","")
                .split(" ");
        if (index > values.length - 1) {
            return null;
        }
        return values[index];
    }

    public String getSessionName() {
        return sessionName;
    }

    public String getOriginUsername() {
        return originUsername;
    }

    public String getOriginSessionId() {
        return originSessionId;
    }

    public String getOriginSessionVersion() {
        return originSessionVersion;
    }

    public String getOriginNetType() {
        return originNetType;
    }

    public String getOriginAddressType() {
        return originAddressType;
    }

    public String getOriginAddress() {
        return originAddress;
    }

    public String getConnectionNetType() {
        return connectionNetType;
    }

    public String getConnectionAddressType() {
        return connectionAddressType;
    }

    public String getConnectionAddress() {
        return connectionAddress;
    }

    public String getMediaType() {
        return mediaType;
    }

    public int getMediaPort() {
        return mediaPort;
    }

    public String getMediaProtocol() {
        return mediaProtocol;
    }

    protected void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    protected void setOriginUsername(String originUsername) {
        this.originUsername = originUsername;
    }

    protected void setOriginSessionId(String originSessionId) {
        this.originSessionId = originSessionId;
    }

    protected void setOriginSessionVersion(String originSessionVersion) {
        this.originSessionVersion = originSessionVersion;
    }

    protected void setOriginNetType(String originNetType) {
        this.originNetType = originNetType;
    }

    protected void setOriginAddressType(String originAddressType) {
        this.originAddressType = originAddressType;
    }

    protected void setOriginAddress(String originAddress) {
        this.originAddress = originAddress;
    }

    protected void setConnectionNetType(String connectionNetType) {
        this.connectionNetType = connectionNetType;
    }

    protected void setConnectionAddressType(String connectionAddressType) {
        this.connectionAddressType = connectionAddressType;
    }

    protected void setConnectionAddress(String connectionAddress) {
        this.connectionAddress = connectionAddress;
    }

    protected void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    protected void setMediaPort(int mediaPort) {
        this.mediaPort = mediaPort;
    }

    protected void setMediaProtocol(String mediaProtocol) {
        this.mediaProtocol = mediaProtocol;
    }

    protected void setSdp(String sdp) {
        this.sdp = sdp;
    }

    /**
     * Generates the SDP for the object
     * Example:
     * v=0\r\n
     * o=root 123 123 IN IP4 127.0.0.1\r\n
     * s=stream\r\n
     * c=IN IP4 127.0.0.1 \r\n
     * t=0 0 \r\n
     * m=audio 49170 RTP/AVP\r\n
     *
     * @return the SDP
     */
    public String toSdp() {
        final StringBuilder b = new StringBuilder();

        if (sdp != null) {
            return sdp;
        }

        b.append("v=0");
        b.append(LB);

        b.append("o=");
        b.append(originUsername);
        b.append(" ");
        b.append(originSessionId);
        b.append(" ");
        b.append(originSessionVersion);
        b.append(" ");
        b.append(originNetType);
        b.append(" ");
        b.append(originAddressType);
        b.append(" ");
        b.append(originAddress);
        b.append(LB);

        b.append("s=");
        b.append(sessionName);
        b.append(LB);

        b.append("c=");
        b.append(connectionNetType);
        b.append(" ");
        b.append(connectionAddressType);
        b.append(" ");
        b.append(connectionAddress);
        b.append(LB);

        b.append("t=0 0");
        b.append(LB);

        b.append("m=");
        b.append(mediaType);
        b.append(" ");
        b.append(mediaPort);
        b.append(" ");
        b.append(mediaProtocol);
        b.append(LB);

        sdp = b.toString();

        return sdp;
    }

    @Override
    public String toString() {
        return toSdp();
    }


}
