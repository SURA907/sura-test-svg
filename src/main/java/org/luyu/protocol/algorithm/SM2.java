package org.luyu.protocol.algorithm;

public class SM2 implements SignatureAlgorithm {
    public static final String TYPE = "SM2";

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
