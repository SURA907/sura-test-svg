package link.luyu.protocol.network;

import link.luyu.protocol.link.Driver;

/** The events call from link layer */
public interface Events {

    /**
     * Send transaction to another resource through router
     *
     * @param account Luyu Account
     * @param tx The transaction to destination blockchain (remain 'key' and 'LuyuSign' empty)
     * @param callback
     */
    void sendTransaction(Account account, Transaction tx, Driver.ReceiptCallback callback);

    /**
     * Call to another resource through router
     *
     * @param account Luyu Account
     * @param request The callRequest to destination blockchain
     * @param callback
     */
    void call(Account account, CallRequest request, Driver.CallResponseCallback callback);

    interface KeyCallback {
        /** @param account chain account */
        void onResponse(Account account);
    }

    /**
     * Get Account by luyu account identity
     *
     * @param identity The identity of luyu account
     * @param callback
     */
    void getAccountByIdentity(String identity, KeyCallback callback);
}
