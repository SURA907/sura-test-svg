package link.luyu.protocol.algorithm;

import java.util.Map;

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

    /**
     * Generate key pair
     *
     * @return (pubKey bytes, secKey bytes)
     */
    Map.Entry<byte[], byte[]> generateKeyPair();
}
