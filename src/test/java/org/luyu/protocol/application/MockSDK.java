package org.luyu.protocol.application;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.luyu.protocol.link.Driver;
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
                new Driver.ReceiptCallback() {
                    @Override
                    public void onResponse(int status, String message, Receipt receipt) {
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
                new Driver.CallResponseCallback() {
                    @Override
                    public void onResponse(int status, String message, CallResponse response) {
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
                new Driver.ReceiptCallback() {
                    @Override
                    public void onResponse(int status, String message, Receipt receipt) {
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
                new Driver.BlockCallback() {
                    @Override
                    public void onResponse(int status, String message, Block block) {
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
                new Driver.BlockCallback() {
                    @Override
                    public void onResponse(int status, String message, Block block) {
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
                new Driver.ResourcesCallback() {
                    @Override
                    public void onResponse(int status, String message, Resource[] resources) {
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
