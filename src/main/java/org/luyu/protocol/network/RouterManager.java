package org.luyu.protocol.network;

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

    void getTransactionReceipt(String chainPath, String txHash, ReceiptCallback callback);

    void getBlockByHash(String chainPath, String blockHash, BlockCallback callback);

    void getBlockByNumber(String chainPath, long blockNumber, BlockCallback callback);

    long getBlockNumber(String chainPath);

    void listResources(String chainPath, ResourcesCallback callback);
}
