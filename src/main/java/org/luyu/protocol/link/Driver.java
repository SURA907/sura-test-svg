package org.luyu.protocol.link;

import org.luyu.protocol.network.Block;
import org.luyu.protocol.network.CallRequest;
import org.luyu.protocol.network.CallResponse;
import org.luyu.protocol.network.Events;
import org.luyu.protocol.network.Receipt;
import org.luyu.protocol.network.Resource;
import org.luyu.protocol.network.Transaction;

public interface Driver {

    public static class STATUS {
        public static final int OK = 0;
        public static final int INTERNAL_ERROR = 100; // driver internal error
        public static final int CONNECTION_EXCEPTION = 200; // query connection exception
        public static final int ROUTER_EXCEPTION = 300; // router exception
    }

    interface ReceiptCallback {
        /**
         * Callback to response receipt
         *
         * @param status Driver.STATUS defined above
         * @param message error message
         * @param receipt
         */
        void onResponse(int status, String message, Receipt receipt);
    }

    interface CallResponseCallback {
        /**
         * Callback to response CallResponse
         *
         * @param status Driver.STATUS defined above
         * @param message error message
         * @param callResponse
         */
        void onResponse(int status, String message, CallResponse callResponse);
    }

    interface BlockCallback {
        /**
         * Callback to response block
         *
         * @param status Driver.STATUS defined above
         * @param message error message
         * @param block
         */
        void onResponse(int status, String message, Block block);
    }

    interface ResourcesCallback {
        /**
         * Callback to response resource list
         *
         * @param status Driver.STATUS defined above
         * @param message error message
         * @param resources
         */
        void onResponse(int status, String message, Resource[] resources);
    }

    /**
     * Query a contract api of blockchain with verifying on-chain proof (generate block)
     *
     * @param request
     * @param callback
     */
    void sendTransaction(Transaction request, ReceiptCallback callback);

    /**
     * Query a contract api of blockchain without verifying on-chain proof (no block generated)
     *
     * @param request
     * @param callback
     */
    void call(CallRequest request, CallResponseCallback callback);

    /**
     * Get a transaction receipt by transaction hash with verifying on-chain proof
     *
     * @param txHash
     * @param callback
     */
    void getTransactionReceipt(String txHash, ReceiptCallback callback);

    /**
     * Get block by hash
     *
     * @param blockHash
     * @param callback
     */
    void getBlockByHash(String blockHash, BlockCallback callback);

    /**
     * Get block by block number
     *
     * @param blockNumber
     * @param callback
     */
    void getBlockByNumber(long blockNumber, BlockCallback callback);

    /**
     * Get latest blockNumber from certain chain
     *
     * @return
     */
    long getBlockNumber();

    /**
     * Sign message with account secret key
     *
     * @param key The secret key of an account
     * @param message The message for signing
     * @return signBytes
     */
    byte[] accountSign(byte[] key, byte[] message);

    /**
     * Verify signature of an account
     *
     * @param identity Account's identity, eg: address or public key
     * @param signBytes Signature with binary encoded
     * @param message The message for signing
     * @return
     */
    boolean accountVerify(byte[] identity, byte[] signBytes, byte[] message);

    /**
     * Get block chain driver type
     *
     * @return
     */
    String getType();

    /**
     * Get resource list belongs to a chain
     *
     * @param callback Return the array of resources
     */
    void listResources(ResourcesCallback callback);

    /**
     * Implement event register logic
     *
     * @param events The event that router manager register in
     */
    void registerEvents(Events events);
}
