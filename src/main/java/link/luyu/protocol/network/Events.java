package link.luyu.protocol.network;

import link.luyu.protocol.link.Driver;

/** The events call from link layer */
public interface Events {

    /**
     * Send transaction to another resource through router
     *
     * @param identity The identity of original blockchain type (who start this transaction)
     * @param tx The transaction to destination blockchain (remain 'key' and 'LuyuSign' empty)
     * @param callback
     */
    void sendTransaction(byte[] identity, Transaction tx, Driver.ReceiptCallback callback);

    /**
     * Call to another resource through router
     *
     * @param identity The identity of original blockchain type (who start this transaction)
     * @param request The callRequest to destination blockchain
     * @param callback
     */
    void call(byte[] identity, CallRequest request, Driver.CallResponseCallback callback);

    interface KeyCallback {
        /** @param account chain account */
        void onResponse(Account account);
    }

    /**
     * Get Account by identity
     *
     * @param identity The identity of original blockchain type. eg: address or public key
     * @param callback
     */
    void getAccountByIdentity(byte[] identity, KeyCallback callback);
}
