package org.luyu.protocol.application;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.luyu.protocol.network.Block;
import org.luyu.protocol.network.CallRequest;
import org.luyu.protocol.network.CallResponse;
import org.luyu.protocol.network.Receipt;
import org.luyu.protocol.network.Resource;
import org.luyu.protocol.network.RouterManager;
import org.luyu.protocol.network.Transaction;

public class MockSDK implements SDK {

    private RouterManager router;

    public void connectRouter(RouterManager router) {
        this.router = router;
    }

    @Override
    public Receipt sendTransaction(Transaction tx) {
        CompletableFuture<Receipt> receiptCompletableFuture = new CompletableFuture<>();
        router.sendTransaction(
                tx,
                new RouterManager.ReceiptCallback() {
                    @Override
                    public void onResponse(Receipt receipt) {
                        receiptCompletableFuture.complete(receipt);
                    }
                });
        try {
            return receiptCompletableFuture.get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public CallResponse call(CallRequest request) {
        return null;
    }

    @Override
    public Receipt getTransactionReceipt(String chainPath, String txHash) {
        return null;
    }

    @Override
    public Block getBlockByHash(String chainPath, String blockHash) {
        return null;
    }

    @Override
    public Block getBlockByNumber(String chainPath, long blockNumber) {
        return null;
    }

    @Override
    public long getBlockNumber(String chainPath) {
        return 0;
    }

    @Override
    public Resource[] listResources(String chainPath) {
        return new Resource[0];
    }
}
