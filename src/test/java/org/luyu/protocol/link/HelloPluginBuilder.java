package org.luyu.protocol.link;

import java.util.Map;

@LuYuPlugin("Hello1.0")
public class HelloPluginBuilder implements PluginBuilder {
    @Override
    public Connection newConnection(Map<String, Object> properties) {
        return null;
    }

    @Override
    public Driver newDriver(Connection connection, Map<String, Object> properties) {
        return null;
    }
}
