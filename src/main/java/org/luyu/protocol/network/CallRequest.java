package org.luyu.protocol.network;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CallRequest {
    private String path; // Path of the calling resource. eg: payment.chain0.hello
    private String method; // Method of resource function name. eg: "transfer"
    private String[] args; // Arguments of function. eg: ["Tom", "100"]
    private long nonce; // Nonce for unique

    private byte[] LuyuSign; // Signature by luyu account

    private Map<String, Object> properties = new HashMap<>(); // Ext params

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    public long getNonce() {
        return nonce;
    }

    public void setNonce(long nonce) {
        this.nonce = nonce;
    }

    public byte[] getLuyuSign() {
        return LuyuSign;
    }

    public void setLuyuSign(byte[] luyuSign) {
        LuyuSign = luyuSign;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    @Override
    public String toString() {
        return "CallRequest{"
                + "path='"
                + path
                + '\''
                + ", method='"
                + method
                + '\''
                + ", args="
                + Arrays.toString(args)
                + ", nonce="
                + nonce
                + ", LuyuSign="
                + Arrays.toString(LuyuSign)
                + ", properties="
                + properties
                + '}';
    }
}
