package org.luyu.protocol.network;

import java.util.HashMap;
import java.util.Map;
import org.luyu.protocol.link.Connection;
import org.luyu.protocol.link.Driver;
import org.luyu.protocol.link.HelloPluginBuilder;
import org.luyu.protocol.utils.Utils;

public class MockRouter implements RouterManager {

    private Map<String, Driver> drivers = new HashMap<>();
    private AccountManager accountManager = new MockAccountManager();

    public MockRouter() {

        HelloPluginBuilder builder = new HelloPluginBuilder();

        String[] chainPaths = {"payment.chain0"};
        for (String chainPath : chainPaths) {
            // Build connection
            Map<String, Object> connectionConfig = new HashMap<>();
            connectionConfig.put("chainPath", chainPath);
            Connection connection = builder.newConnection(connectionConfig);

            // Build driver
            Map<String, Object> driverConfig = new HashMap<>();
            driverConfig.put("chainPath", chainPath);
            Driver driver = builder.newDriver(connection, driverConfig);
            this.drivers.put(chainPath, driver);
        }
    }

    @Override
    public void sendTransaction(Transaction tx, ReceiptCallback callback) {
        ChainAccount chainAccount = accountManager.verifyAndGetChainAccount(tx);
        if (chainAccount != null) {
            tx.setKey(chainAccount.getSecKey());

            System.out.println("==> SendTransaction: " + tx.toString());
            String chainPath = Utils.getChainPath(tx.getPath());
            Driver driver = drivers.get(chainPath);
            driver.sendTransaction(
                    tx,
                    new Driver.ReceiptCallback() {
                        @Override
                        public void onResponse(int status, String message, Receipt receipt) {
                            if (status == Driver.STATUS.OK) {
                                System.out.println("==> Receipt: " + receipt.toString());
                                callback.onResponse(receipt);
                            } else {
                                System.out.println(message);
                                callback.onResponse(null);
                            }
                        }
                    });
        }
    }

    @Override
    public void call(CallRequest request, CallResponseCallback callback) {
        System.out.println("==> Call: " + request.toString());
        String chainPath = Utils.getChainPath(request.getPath());
        Driver driver = drivers.get(chainPath);
        driver.call(
                request,
                new Driver.CallResponseCallback() {
                    @Override
                    public void onResponse(int status, String message, CallResponse response) {
                        if (status == Driver.STATUS.OK) {
                            System.out.println("==> CallResponse: " + response.toString());
                            callback.onResponse(response);
                        } else {
                            System.out.println(message);
                            callback.onResponse(null);
                        }
                    }
                });
    }

    @Override
    public void getTransactionReceipt(String chainPath, String txHash, ReceiptCallback callback) {
        System.out.println("==> GetTransactionReceipt: " + txHash);
        Driver driver = drivers.get(Utils.getChainPath(chainPath));
        driver.getTransactionReceipt(
                txHash,
                new Driver.ReceiptCallback() {
                    @Override
                    public void onResponse(int status, String message, Receipt receipt) {
                        if (status == Driver.STATUS.OK) {
                            System.out.println("==> Receipt: " + receipt.toString());
                            callback.onResponse(receipt);
                        } else {
                            System.out.println(message);
                            callback.onResponse(null);
                        }
                    }
                });
    }

    @Override
    public void getBlockByHash(String chainPath, String blockHash, BlockCallback callback) {
        Driver driver = drivers.get(Utils.getChainPath(chainPath));
        driver.getBlockByHash(
                blockHash,
                new Driver.BlockCallback() {
                    @Override
                    public void onResponse(int status, String message, Block block) {
                        if (status == Driver.STATUS.OK) {
                            System.out.println("==> Block: " + block.toString());
                            callback.onResponse(block);
                        } else {
                            System.out.println(message);
                            callback.onResponse(null);
                        }
                    }
                });
    }

    @Override
    public void getBlockByNumber(String chainPath, long blockNumber, BlockCallback callback) {
        Driver driver = drivers.get(Utils.getChainPath(chainPath));
        driver.getBlockByNumber(
                blockNumber,
                new Driver.BlockCallback() {
                    @Override
                    public void onResponse(int status, String message, Block block) {
                        if (status == Driver.STATUS.OK) {
                            System.out.println("==> Block: " + block.toString());
                            callback.onResponse(block);
                        } else {
                            System.out.println(message);
                            callback.onResponse(null);
                        }
                    }
                });
    }

    @Override
    public long getBlockNumber(String chainPath) {
        Driver driver = drivers.get(Utils.getChainPath(chainPath));
        return driver.getBlockNumber();
    }

    @Override
    public void listResources(String chainPath, ResourcesCallback callback) {
        Driver driver = drivers.get(Utils.getChainPath(chainPath));
        driver.listResources(
                new Driver.ResourcesCallback() {
                    @Override
                    public void onResponse(int status, String message, Resource[] resources) {
                        if (status == Driver.STATUS.OK) {
                            System.out.println("==> Resources: size " + resources.length);
                            callback.onResponse(resources);
                        } else {
                            System.out.println(message);
                            callback.onResponse(null);
                        }
                    }
                });
    }
}
