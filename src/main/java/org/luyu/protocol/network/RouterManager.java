package org.luyu.protocol.network;

import org.luyu.protocol.link.Driver;

public interface RouterManager {

    interface ReceiptCallback {
        void onResponse(Receipt receipt);
    }

    interface CallResponseCallback {
        void onResponse(CallResponse response);
    }

    interface BlockCallback {
        void onResponse(Block block);
    }

    interface ResourcesCallback {
        void onResponse(Resource[] resources);
    }

    void sendTransaction(Transaction tx, ReceiptCallback callback);

    void call(CallRequest request, CallResponseCallback callback);

    void getTransactionReceipt(String txHash, ReceiptCallback callback);

    void getBlockByHash(String blockHash, BlockCallback callback);

    void getBlockByNumber(long blockNumber, BlockCallback callback);

    void listResources(String chainPath, Driver.ResourcesCallback callback);
}
