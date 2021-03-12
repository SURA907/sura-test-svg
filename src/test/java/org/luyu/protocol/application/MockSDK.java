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
        CompletableFuture<Receipt> future = new CompletableFuture<>();
        router.sendTransaction(
                tx,
                new RouterManager.ReceiptCallback() {
                    @Override
                    public void onResponse(Receipt receipt) {
                        future.complete(receipt);
                    }
                });
        try {
            return future.get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public CallResponse call(CallRequest request) {

        CompletableFuture<CallResponse> future = new CompletableFuture<>();
        router.call(
                request,
                new RouterManager.CallResponseCallback() {
                    @Override
                    public void onResponse(CallResponse response) {
                        future.complete(response);
                    }
                });
        try {
            return future.get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Receipt getTransactionReceipt(String chainPath, String txHash) {
        CompletableFuture<Receipt> future = new CompletableFuture<>();
        router.getTransactionReceipt(
                chainPath,
                txHash,
                new RouterManager.ReceiptCallback() {
                    @Override
                    public void onResponse(Receipt receipt) {
                        future.complete(receipt);
                    }
                });
        try {
            return future.get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Block getBlockByHash(String chainPath, String blockHash) {

        CompletableFuture<Block> future = new CompletableFuture<>();
        router.getBlockByHash(
                chainPath,
                blockHash,
                new RouterManager.BlockCallback() {
                    @Override
                    public void onResponse(Block block) {
                        future.complete(block);
                    }
                });
        try {
            return future.get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Block getBlockByNumber(String chainPath, long blockNumber) {

        CompletableFuture<Block> future = new CompletableFuture<>();
        router.getBlockByNumber(
                chainPath,
                blockNumber,
                new RouterManager.BlockCallback() {
                    @Override
                    public void onResponse(Block block) {
                        future.complete(block);
                    }
                });
        try {
            return future.get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public long getBlockNumber(String chainPath) {
        return router.getBlockNumber(chainPath);
    }

    @Override
    public Resource[] listResources(String chainPath) {

        CompletableFuture<Resource[]> future = new CompletableFuture<>();
        router.listResources(
                chainPath,
                new RouterManager.ResourcesCallback() {
                    @Override
                    public void onResponse(Resource[] resources) {
                        future.complete(resources);
                    }
                });
        try {
            return future.get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            return null;
        }
    }
}
