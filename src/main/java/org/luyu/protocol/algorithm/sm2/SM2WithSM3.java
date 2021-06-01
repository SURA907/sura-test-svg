package org.luyu.protocol.algorithm.sm2;

import org.luyu.protocol.algorithm.SignatureAlgorithm;

public class SM2WithSM3 implements SignatureAlgorithm {
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
