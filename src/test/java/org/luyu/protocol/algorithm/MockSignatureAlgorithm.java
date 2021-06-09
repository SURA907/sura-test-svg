package org.luyu.protocol.algorithm;

import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;

public class MockSignatureAlgorithm implements SignatureAlgorithm {
    public static final String TYPE = "MOCK_SIGNATURE";

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public byte[] sign(byte[] secKey, byte[] message) {
        return (new String("sign") + String.valueOf(message)).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public boolean verify(byte[] pubKey, byte[] signBytes, byte[] message) {
        return Arrays.equals(signBytes, sign(new byte[] {}, message));
    }

    @Override
    public Map.Entry<byte[], byte[]> generateKeyPair() {
        return new AbstractMap.SimpleEntry<>(new byte[0], new byte[0]);
    }
}
