package org.luyu.protocol.algorithm.ecdsa.secp256r1;

import java.util.Map;
import org.luyu.protocol.algorithm.SignatureAlgorithm;

public class EcdsaSecp256r1 implements SignatureAlgorithm {
    public static final String TYPE = "ECDSA_SECP256R1_WITH_SHA256";

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public byte[] sign(byte[] secKey, byte[] message) {
        return new byte[0];
    }

    @Override
    public boolean verify(byte[] pubKey, byte[] signBytes, byte[] message) {
        return false;
    }

    @Override
    public Map.Entry<byte[], byte[]> generateKeyPair() {
        return null;
    }
}
