package org.luyu.protocol.network;

import java.util.Arrays;

public class Receipt {
    private String[] result; // Resource function's return output
    private int code; // Error code of blockchain
    private String message; // Error message of blockchain
    private String path; // Transaction path of the calling resource. eg: payment.chain0.hello
    private String method; // Transaction method of resource function name. eg: "transfer"
    private String[] args; // Transaction arguments of function. eg: ["Tom", "100"]
    private String transactionHash; // Original transaction hash
    private byte[] transactionBytes; // The original transaction bytes of a certain blockchain
    private long blockNumber; // Block number of this transaction belongs to

    private String version; // version of luyu protocol

    public String[] getResult() {
        return result;
    }

    public void setResult(String[] result) {
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public String getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }

    public byte[] getTransactionBytes() {
        return transactionBytes;
    }

    public void setTransactionBytes(byte[] transactionBytes) {
        this.transactionBytes = transactionBytes;
    }

    public long getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(long blockNumber) {
        this.blockNumber = blockNumber;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "Receipt{"
                + "result="
                + Arrays.toString(result)
                + ", code="
                + code
                + ", message='"
                + message
                + '\''
                + ", path='"
                + path
                + '\''
                + ", method='"
                + method
                + '\''
                + ", args="
                + Arrays.toString(args)
                + ", transactionHash='"
                + transactionHash
                + '\''
                + ", transactionBytes="
                + Arrays.toString(transactionBytes)
                + ", blockNumber="
                + blockNumber
                + ", version='"
                + version
                + '\''
                + '}';
    }
}
