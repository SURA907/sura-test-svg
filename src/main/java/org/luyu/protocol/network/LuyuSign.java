package org.luyu.protocol.network;

public interface LuyuSign {
    byte[] sign(byte[] secKey, LuyuSignData data);

    boolean verify(byte[] signBytes, LuyuSignData data);

    String recover(byte[] signBytes, LuyuSignData data);

    String secKey2Address(byte[] secKey);

    String pubKey2Address(byte[] pubKey);
}
