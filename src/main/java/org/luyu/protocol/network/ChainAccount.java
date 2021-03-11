package org.luyu.protocol.network;

import java.util.HashMap;
import java.util.Map;

public class ChainAccount {
    private Map<String, Object> properties = new HashMap<>();

    private String name;
    private String type;
    private byte[] identity;
    private byte[] secKey;
    private byte[] pubKey;

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public byte[] getIdentity() {
        return identity;
    }

    public void setIdentity(byte[] identity) {
        this.identity = identity;
    }

    public byte[] getSecKey() {
        return secKey;
    }

    public void setSecKey(byte[] secKey) {
        this.secKey = secKey;
    }

    public byte[] getPubKey() {
        return pubKey;
    }

    public void setPubKey(byte[] pubKey) {
        this.pubKey = pubKey;
    }
}
