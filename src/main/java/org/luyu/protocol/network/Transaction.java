package org.luyu.protocol.network;

import java.util.Arrays;

public class Transaction {
    private String path; // Path of the calling resource. eg: payment.chain0.hello
    private String method; // Method of resource function name. eg: "transfer"
    private String[] args; // Arguments of function. eg: ["Tom", "100"]
    private long nonce; // Nonce for unique

    // Either key or LuyuSign
    private byte[] key; // Secret key of a certain blockchain

    // Either key or LuyuSign, if key not set, use this sign to query AccountManager
    private byte[] LuyuSign; // Signature by luyu account

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

    public byte[] getKey() {
        return key;
    }

    public void setKey(byte[] key) {
        this.key = key;
    }

    public byte[] getLuyuSign() {
        return LuyuSign;
    }

    public void setLuyuSign(byte[] luyuSign) {
        LuyuSign = luyuSign;
    }

    @Override
    public String toString() {
        return "Transaction{"
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
                + '}';
    }
}
