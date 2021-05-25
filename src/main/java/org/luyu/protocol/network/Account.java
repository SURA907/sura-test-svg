package org.luyu.protocol.network;

public interface Account {
    /**
     * Get identity of this account. eg: address or public key
     *
     * @return
     */
    byte[] getIdentity();

    /**
     * Sign message with this account secret key
     *
     * @param message The message for signing
     * @return signBytes
     */
    byte[] sign(byte[] message);

    /**
     * Verify signature of this account
     *
     * @param signBytes Signature with binary encoded
     * @param message The message for signing
     * @return
     */
    boolean verify(byte[] signBytes, byte[] message);
}
