package org.luyu.protocol.network;

public interface Account {
    public static class STATUS {
        public static final int OK = 0;
        public static final int ACCOUNT_MANAGER_EXCEPTION = 1000; // router exception
    }

    interface SignCallback {
        /**
         * @param status Account.STATUS defined above
         * @param message Error message
         * @param signBytes Signature bytes
         */
        void onResponse(int status, String message, byte[] signBytes);
    }

    interface VerifyCallback {

        /**
         * @param status Account.STATUS defined above
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
}
