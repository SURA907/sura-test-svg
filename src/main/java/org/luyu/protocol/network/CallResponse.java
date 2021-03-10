package org.luyu.protocol.network;

public class CallResponse {
    private String result; // Resource function's return output
    private long code; // Error code
    private String message; // Error message
    private String path; // Transaction path of the calling resource. eg: payment.chain0.hello
    private String method; // Transaction method of resource function name. eg: "transfer"
    private String[] args; // Transaction arguments of function. eg: ["Tom", "100"]

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public long getCode() {
        return code;
    }

    public void setCode(long code) {
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
}
