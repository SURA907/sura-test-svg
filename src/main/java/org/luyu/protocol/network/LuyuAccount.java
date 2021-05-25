package org.luyu.protocol.network;

import java.util.HashMap;
import java.util.Map;

// Luyu Account
public abstract class LuyuAccount {
    private Map<String, Object> properties = new HashMap<>();

    private String name;
    private byte[] identity;

    public abstract byte[] chainAccountSign(String algorithm, String usage, byte[] message);

    public abstract boolean chainAccountVerify(
            String algorithm, String usage, byte[] sign, byte[] message);
}
