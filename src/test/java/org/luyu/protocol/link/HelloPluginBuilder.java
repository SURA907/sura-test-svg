package org.luyu.protocol.link;

import java.util.Map;

@LuyuPlugin("Hello1.0")
public class HelloPluginBuilder implements PluginBuilder {
    @Override
    public Connection newConnection(Map<String, Object> properties) {
        try {
            return new HelloConnection(properties);
        } catch (Exception e) {
            System.out.println("HelloPluginBuilde newConnection exception:" + e.toString());
            return null;
        }
    }

    @Override
    public Driver newDriver(Connection connection, Map<String, Object> properties) {
        try {
            return new HelloDriver(connection, properties);
        } catch (Exception e) {
            System.out.println("HelloPluginBuilde newDriver exception:" + e.toString());
            return null;
        }
    }
}
