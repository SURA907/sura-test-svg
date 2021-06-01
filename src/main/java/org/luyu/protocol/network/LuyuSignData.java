package org.luyu.protocol.network;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;

public class LuyuSignData {
    private static ObjectMapper objectMapper = new ObjectMapper();
    private String path; // Path of the calling resource. eg: payment.chain0.hello
    private String method; // Method of resource function name. eg: "transfer"
    private String[] args; // Arguments of function. eg: ["Tom", "100"]
    private long nonce; // Nonce for unique
    private String sender; // sender of this transaction

    public LuyuSignData() {}

    public LuyuSignData(Transaction tx) {
        setPath(tx.getPath());
        setMethod(tx.getMethod());
        setArgs(tx.getArgs());
        setNonce(tx.getNonce());
        setSender(tx.getSender());
    }

    public LuyuSignData(CallRequest request) {
        setPath(request.getPath());
        setMethod(request.getMethod());
        setArgs(request.getArgs());
        setNonce(request.getNonce());
        setSender(request.getSender());
    }

    public byte[] toBytes() throws Exception {
        // json encoding
        String str = objectMapper.writeValueAsString(this);
        return str.getBytes(StandardCharsets.UTF_8);
    }

    public static LuyuSignData parseFrom(byte[] bytes) throws Exception {
        LuyuSignData data = objectMapper.readValue(bytes, LuyuSignData.class);
        return data;
    }

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

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
