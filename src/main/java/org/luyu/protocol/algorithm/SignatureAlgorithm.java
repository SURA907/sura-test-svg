package org.luyu.protocol.algorithm;

public interface SignatureAlgorithm {
    /**
     * Get signature type defined in SignatureType.java
     *
     * @return
     */
    String getType();

    /**
     * Sign message
     *
     * @param secKey
     * @param message The message for signing
     * @return signBytes
     */
    byte[] sign(byte[] secKey, byte[] message);

    /**
     * Verify signature
     *
     * @param pubKey
     * @param signBytes Signature with binary encoded
     * @param message The message for signing
     * @return
     */
    boolean verify(byte[] pubKey, byte[] signBytes, byte[] message);
}
