package org.luyu.protocol.network;

import java.util.Arrays;
import java.util.Map;

public class Resource {
    private String path; // Path of the resource. eg: payment.chain0.hello
    private String type; // Blockchain type that the resource belongs to
    private String[]
            methods; // Method list of resource function name. eg: ["transfer(2)", "balanceOf(1)"]
    private Map<String, Object> properties; // Other property if needed

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String[] getMethods() {
        return methods;
    }

    public void setMethods(String[] methods) {
        this.methods = methods;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    @Override
    public String toString() {
        return "Resource{"
                + "path='"
                + path
                + '\''
                + ", type='"
                + type
                + '\''
                + ", methods="
                + Arrays.toString(methods)
                + ", properties="
                + properties
                + '}';
    }
}
