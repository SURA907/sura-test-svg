package org.luyu.protocol.link;

import java.util.Map;

@LuyuPlugin("Hello1.0")
public class HelloPluginBuilder implements PluginBuilder {
    @Override
    public Connection newConnection(Map<String, Object> properties) {
        return new HelloConnection(properties);
    }

    @Override
    public Driver newDriver(Connection connection, Map<String, Object> properties) {
        return new HelloDriver(connection, properties);
    }
}
