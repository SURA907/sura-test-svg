package link.luyu.protocol.application;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import link.luyu.protocol.common.STATUS;
import link.luyu.protocol.link.Driver;
import link.luyu.protocol.network.Block;
import link.luyu.protocol.network.CallRequest;
import link.luyu.protocol.network.CallResponse;
import link.luyu.protocol.network.Receipt;
import link.luyu.protocol.network.Resource;
import link.luyu.protocol.network.RouterManager;
import link.luyu.protocol.network.Transaction;

public class MockSDK implements SDK {

    private RouterManager router;

    public void connectRouter(RouterManager router) {
        this.router = router;
    }

    @Override
    public RemoteCall<Receipt> sendTransaction(Transaction tx) {
        RemoteCall<Receipt> caller =
                new RemoteCall<Receipt>() {
                    @Override
                    public Receipt send() {
                        CompletableFuture<Receipt> future = new CompletableFuture<>();
                        router.sendTransaction(
                                tx,
                                new Driver.ReceiptCallback() {
                                    @Override
                                    public void onResponse(
                                            int status, String message, Receipt receipt) {
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
                    public void asyncSend(Callback<Receipt> callback) {
                        router.sendTransaction(
                                tx,
                                new Driver.ReceiptCallback() {
                                    @Override
                                    public void onResponse(
                                            int status, String message, Receipt receipt) {
                                        callback.onResponse(status, message, receipt);
                                    }
                                });
                    }
                };
        return caller;
    }

    @Override
    public RemoteCall<CallResponse> call(CallRequest request) {
        RemoteCall<CallResponse> caller =
                new RemoteCall<CallResponse>() {
                    @Override
                    public CallResponse send() {
                        CompletableFuture<CallResponse> future = new CompletableFuture<>();
                        router.call(
                                request,
                                new Driver.CallResponseCallback() {
                                    @Override
                                    public void onResponse(
                                            int status, String message, CallResponse response) {
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
                    public void asyncSend(Callback<CallResponse> callback) {
                        router.call(
                                request,
                                new Driver.CallResponseCallback() {
                                    @Override
                                    public void onResponse(
                                            int status, String message, CallResponse response) {
                                        callback.onResponse(status, message, response);
                                    }
                                });
                    }
                };

        return caller;
    }

    @Override
    public RemoteCall<Receipt> getTransactionReceipt(String chainPath, String txHash) {
        RemoteCall<Receipt> caller =
                new RemoteCall<Receipt>() {
                    @Override
                    public Receipt send() {
                        CompletableFuture<Receipt> future = new CompletableFuture<>();
                        router.getTransactionReceipt(
                                chainPath,
                                txHash,
                                new Driver.ReceiptCallback() {
                                    @Override
                                    public void onResponse(
                                            int status, String message, Receipt receipt) {
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
                    public void asyncSend(Callback<Receipt> callback) {
                        router.getTransactionReceipt(
                                chainPath,
                                txHash,
                                new Driver.ReceiptCallback() {
                                    @Override
                                    public void onResponse(
                                            int status, String message, Receipt receipt) {
                                        callback.onResponse(status, message, receipt);
                                    }
                                });
                    }
                };
        return caller;
    }

    @Override
    public RemoteCall<Block> getBlockByHash(String chainPath, String blockHash) {
        RemoteCall<Block> caller =
                new RemoteCall<Block>() {
                    @Override
                    public Block send() {
                        CompletableFuture<Block> future = new CompletableFuture<>();
                        router.getBlockByHash(
                                chainPath,
                                blockHash,
                                new Driver.BlockCallback() {
                                    @Override
                                    public void onResponse(
                                            int status, String message, Block block) {
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
                    public void asyncSend(Callback<Block> callback) {
                        router.getBlockByHash(
                                chainPath,
                                blockHash,
                                new Driver.BlockCallback() {
                                    @Override
                                    public void onResponse(
                                            int status, String message, Block block) {
                                        callback.onResponse(status, message, block);
                                    }
                                });
                    }
                };
        return caller;
    }

    @Override
    public RemoteCall<Block> getBlockByNumber(String chainPath, long blockNumber) {
        RemoteCall<Block> caller =
                new RemoteCall<Block>() {
                    @Override
                    public Block send() {
                        CompletableFuture<Block> future = new CompletableFuture<>();
                        router.getBlockByNumber(
                                chainPath,
                                blockNumber,
                                new Driver.BlockCallback() {
                                    @Override
                                    public void onResponse(
                                            int status, String message, Block block) {
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
                    public void asyncSend(Callback<Block> callback) {
                        router.getBlockByNumber(
                                chainPath,
                                blockNumber,
                                new Driver.BlockCallback() {
                                    @Override
                                    public void onResponse(
                                            int status, String message, Block block) {
                                        callback.onResponse(status, message, block);
                                    }
                                });
                    }
                };
        return caller;
    }

    @Override
    public RemoteCall<Long> getBlockNumber(String chainPath) {
        RemoteCall<Long> caller =
                new RemoteCall<Long>() {
                    @Override
                    public Long send() {
                        return router.getBlockNumber(chainPath);
                    }

                    @Override
                    public void asyncSend(Callback<Long> callback) {
                        callback.onResponse(STATUS.OK, "Success", router.getBlockNumber(chainPath));
                    }
                };
        return caller;
    }

    @Override
    public RemoteCall<Resource[]> listResources(String chainPath) {
        RemoteCall<Resource[]> caller =
                new RemoteCall<Resource[]>() {
                    @Override
                    public Resource[] send() {
                        CompletableFuture<Resource[]> future = new CompletableFuture<>();
                        router.listResources(
                                chainPath,
                                new Driver.ResourcesCallback() {
                                    @Override
                                    public void onResponse(
                                            int status, String message, Resource[] resources) {
                                        future.complete(resources);
                                    }
                                });
                        try {
                            return future.get(10, TimeUnit.SECONDS);
                        } catch (Exception e) {
                            return null;
                        }
                    }

                    @Override
                    public void asyncSend(Callback<Resource[]> callback) {
                        router.listResources(
                                chainPath,
                                new Driver.ResourcesCallback() {
                                    @Override
                                    public void onResponse(
                                            int status, String message, Resource[] resources) {
                                        callback.onResponse(status, message, resources);
                                    }
                                });
                    }
                };
        return caller;
    }
}
