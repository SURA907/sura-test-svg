package org.luyu.protocol.link;

import java.util.Map;

/** Use config to build link layer objects */
public interface PluginBuilder {
    /**
     * Used by router to build connection using properties parsed from config file
     *
     * @param properties Eg: ip:port of a blockchain
     * @return
     */
    Connection newConnection(Map<String, Object> properties);

    /**
     * Used by router to build driver using properties parsed from config file
     *
     * @param connection The connection interface for sending binary message to blockchain
     * @param properties Eg: block header signatures' public key
     * @return
     */
    Driver newDriver(Connection connection, Map<String, Object> properties);
}
