package link.luyu.protocol.network;

import link.luyu.protocol.link.Driver;

public interface RouterManager {

    void sendTransaction(Transaction tx, Driver.ReceiptCallback callback);

    void call(CallRequest request, Driver.CallResponseCallback callback);

    void getTransactionReceipt(String chainPath, String txHash, Driver.ReceiptCallback callback);

    void getBlockByHash(String chainPath, String blockHash, Driver.BlockCallback callback);

    void getBlockByNumber(String chainPath, long blockNumber, Driver.BlockCallback callback);

    long getBlockNumber(String chainPath);

    void listResources(String chainPath, Driver.ResourcesCallback callback);
}
