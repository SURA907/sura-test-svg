package org.luyu.protocol.link;

import org.luyu.protocol.network.Block;
import org.luyu.protocol.network.CallRequest;
import org.luyu.protocol.network.CallResponse;
import org.luyu.protocol.network.Events;
import org.luyu.protocol.network.Receipt;
import org.luyu.protocol.network.Resource;
import org.luyu.protocol.network.Transaction;

public interface Driver {

    interface ReceiptCallback {
        void onResponse(Receipt receipt);
    }

    interface CallResponseCallback {
        void onResponse(CallResponse callResponse);
    }

    interface BlockCallback {
        void onResponse(Block block);
    }

    interface ResourcesCallback {
        void onResponse(Resource[] resources);
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
     * @param chainPath Eg: payment.chain0
     * @param callback Return the array of resources
     */
    void listResources(String chainPath, ResourcesCallback callback);

    /**
     * Call function in events to call router logic
     *
     * @param events
     */
    void onChainEvent(Events events);
}
