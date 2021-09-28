package link.luyu.protocol.network;

public interface Account {

    interface SignCallback {
        /**
         * @param status common.STATUS
         * @param message Error message
         * @param signBytes Signature bytes
         */
        void onResponse(int status, String message, byte[] signBytes);
    }

    interface VerifyCallback {

        /**
         * @param status common.STATUS
         * @param message Error message
         * @param verifyResult verify result
         */
        void onResponse(int status, String message, boolean verifyResult);
    }

    /**
     * Get public key of this account
     *
     * @return
     */
    byte[] getPubKey();

    /**
     * Sign message with this account secret key
     *
     * @param message The message for signing
     * @param callback
     */
    void sign(byte[] message, SignCallback callback);

    /**
     * Verify signature of this account
     *
     * @param signBytes Signature with binary encoded
     * @param message The message for signing
     * @param callback
     */
    void verify(byte[] signBytes, byte[] message, VerifyCallback callback);

    /**
     * Set property for this algorithm account. Plugin can save some extra data of this account if
     * needed. Notices that the properties are belongs to the keyPair, if default keyPair change,
     * properties would be changed.
     *
     * @param key The key of the value
     * @param value The value of the key
     */
    void setProperty(String key, String value);

    /**
     * Get property for this algorithm account of a certain key
     *
     * @param key The key of the value
     * @return Return null if value is not existing
     */
    String getProperty(String key);
}
