package com.upptalk.jinglertpengine.ng.protocol;

import java.io.Serializable;

/**
 * SDP Session Description Protocol
 * data class
 * @author bhlangonijr
 *         Date: 4/18/14
 *         Time: 5:42 PM
 */
public class Sdp implements Serializable {

    private String sessionName; // s=
    private String originUsername; // o=[0]
    private String originAddressType; // o=[4]
    private String originAddress; // o=[5]
    private String connectionAddressType; // c=[1]
    private String connectionAddress; // c=[2]
    private String mediaType; // m=[0]
    private int mediaPort; //m=[1]

    public Sdp(String sdp) {
        for (String line: sdp.split("\n")) {
            final String field[] = line.split("=");
            if (field[0].equals("s")) {
                sessionName = field[1];
            } else if (field[0].equals("o")) {
                originUsername = getItem(field[1], 0);
                originAddressType = getItem(field[1], 4);
                originAddress = getItem(field[1], 5);
            } else if (field[0].equals("c")) {
                connectionAddressType = getItem(field[1], 1);
                connectionAddress = getItem(field[1], 2);
            } else if (field[0].equals("m")) {
                mediaType = getItem(field[1], 0);
                mediaPort = Integer.parseInt(getItem(field[1], 1));
            }
        }
    }

    private static final String getItem(String fieldValue, int index) {
        final String[] values = fieldValue.split(" ");
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

    public String getOriginAddressType() {
        return originAddressType;
    }

    public String getOriginAddress() {
        return originAddress;
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

    @Override
    public String toString() {
        return "Sdp{" +
                "sessionName='" + sessionName + '\'' +
                ", originUsername='" + originUsername + '\'' +
                ", originAddressType='" + originAddressType + '\'' +
                ", originAddress='" + originAddress + '\'' +
                ", connectionAddressType='" + connectionAddressType + '\'' +
                ", connectionAddress='" + connectionAddress + '\'' +
                ", mediaType='" + mediaType + '\'' +
                ", mediaPort=" + mediaPort +
                '}';
    }
}
