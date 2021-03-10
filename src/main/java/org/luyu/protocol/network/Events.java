package org.luyu.protocol.network;

/** The events call from link layer */
public interface Events {

    /**
     * Send transaction to another resource through router
     *
     * @param identity The identity of original blockchain type (who start this transaction)
     * @param tx The transaction to destination blockchain (remain 'key' and 'LuyuSign' empty)
     * @param callback
     */
    void sendTransaction(byte[] identity, Transaction tx, RouterManager.ReceiptCallback callback);

    /**
     * Call to another resource through router
     *
     * @param request The callRequest to destination blockchain
     * @param callback
     */
    void call(CallRequest request, RouterManager.CallResponseCallback callback);

    interface KeyCallback {
        void onResponse(byte[] key);
    }

    void getKeyByIdentity(byte[] identity, KeyCallback callback);
}
