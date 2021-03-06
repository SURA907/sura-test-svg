package link.luyu.protocol.network;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import link.luyu.protocol.common.Default;

public class Transaction {
    private String path; // Path of the calling resource. eg: payment.chain0.hello
    private String method; // Method of resource function name. eg: "transfer"
    private String[] args; // Arguments of function. eg: ["Tom", "100"]
    private long nonce; // Nonce for unique
    private String sender; // sender of this transaction

    private final String version = Default.PROTOCOL_VERSION; // version of luyu protocol

    private byte[] LuyuSign; // Signature by luyu account

    // Ext params
    private Map<String, Object> properties = new HashMap<>();

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

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getVersion() {
        return version;
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
                + ", sender='"
                + sender
                + '\''
                + ", version='"
                + version
                + '\''
                + ", LuyuSign="
                + Arrays.toString(LuyuSign)
                + ", properties="
                + properties
                + '}';
    }
}
