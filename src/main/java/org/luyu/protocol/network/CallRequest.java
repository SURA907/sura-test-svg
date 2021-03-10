package org.luyu.protocol.network;

public class CallRequest {
    private String path; // Path of the calling resource. eg: payment.chain0.hello
    private String method; // Method of resource function name. eg: "transfer"
    private String[] args; // Arguments of function. eg: ["Tom", "100"]

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
