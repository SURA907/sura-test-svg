package org.luyu.protocol.network;

import java.util.HashMap;
import java.util.Map;

public class ChainAccount {
    private Map<String, Object> properties = new HashMap<>();

    private String name;
    private String type;
    private byte[] identity;
    private byte[] secKey;
    private byte[] pubKey;
}
