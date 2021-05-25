package org.luyu.protocol.algorithm;

public class EcdsaSecp256k1 implements SignatureAlgorithm {
    public static final String TYPE = "ECDSA_WITH_SECP256K1";

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
}
