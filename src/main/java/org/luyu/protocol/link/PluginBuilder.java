package org.luyu.protocol.link;

import java.util.Map;
import org.luyu.protocol.common.Default;

/** Use config to build link layer objects */
public abstract class PluginBuilder {

    /** @return Luyu protocl version */
    public static String getProtocolVersion() {
        return Default.PROTOCOL_VERSION;
    }

    /**
     * Used by router to build connection using properties parsed from config file
     *
     * @param properties Eg: ip:port of a blockchain
     * @return
     */
    public abstract Connection newConnection(Map<String, Object> properties);

    /**
     * Used by router to build driver using properties parsed from config file
     *
     * @param connection The connection interface for sending binary message to blockchain
     * @param properties Eg: block header signatures' public key
     * @return
     */
    public abstract Driver newDriver(Connection connection, Map<String, Object> properties);
}
