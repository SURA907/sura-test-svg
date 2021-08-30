package link.luyu.protocol.network;

import java.util.HashMap;
import java.util.Map;
import link.luyu.protocol.common.STATUS;
import link.luyu.protocol.link.Connection;
import link.luyu.protocol.link.Driver;
import link.luyu.protocol.link.HelloPluginBuilder;
import link.luyu.protocol.utils.Utils;

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
    public void sendTransaction(Transaction tx, Driver.ReceiptCallback callback) {
        System.out.println("==> SendTransaction: " + tx.toString());
        String chainPath = Utils.getChainPath(tx.getPath());
        Driver driver = drivers.get(chainPath);
        try {
            Account account =
                    accountManager.getAccountBySignature(
                            driver.getSignatureType(), tx.getLuyuSign(), new LuyuSignData(tx));

            driver.sendTransaction(
                    account,
                    tx,
                    new Driver.ReceiptCallback() {
                        @Override
                        public void onResponse(int status, String message, Receipt receipt) {
                            if (status == STATUS.OK) {
                                System.out.println("==> Receipt: " + receipt.toString());
                                callback.onResponse(status, message, receipt);
                            } else {
                                System.out.println(message);
                                callback.onResponse(status, message, receipt);
                            }
                        }
                    });
        } catch (Exception e) {
            System.out.println("unexpected exception: " + e.toString());
        }
    }

    @Override
    public void call(CallRequest request, Driver.CallResponseCallback callback) {
        System.out.println("==> Call: " + request.toString());
        String chainPath = Utils.getChainPath(request.getPath());
        Driver driver = drivers.get(chainPath);

        try {
            Account account =
                    accountManager.getAccountBySignature(
                            driver.getSignatureType(),
                            request.getLuyuSign(),
                            new LuyuSignData(request));

            driver.call(
                    account,
                    request,
                    new Driver.CallResponseCallback() {
                        @Override
                        public void onResponse(int status, String message, CallResponse response) {
                            if (status == STATUS.OK) {
                                System.out.println("==> CallResponse: " + response.toString());
                                callback.onResponse(status, message, response);
                            } else {
                                System.out.println(message);
                                callback.onResponse(status, message, response);
                            }
                        }
                    });
        } catch (Exception e) {
            System.out.println("unexpected exception: " + e.toString());
        }
    }

    @Override
    public void getTransactionReceipt(
            String chainPath, String txHash, Driver.ReceiptCallback callback) {
        System.out.println("==> GetTransactionReceipt: " + txHash);
        Driver driver = drivers.get(Utils.getChainPath(chainPath));
        driver.getTransactionReceipt(
                txHash,
                new Driver.ReceiptCallback() {
                    @Override
                    public void onResponse(int status, String message, Receipt receipt) {
                        if (status == STATUS.OK) {
                            System.out.println("==> Receipt: " + receipt.toString());
                            callback.onResponse(status, message, receipt);
                        } else {
                            System.out.println(message);
                            callback.onResponse(status, message, receipt);
                        }
                    }
                });
    }

    @Override
    public void getBlockByHash(String chainPath, String blockHash, Driver.BlockCallback callback) {
        Driver driver = drivers.get(Utils.getChainPath(chainPath));
        driver.getBlockByHash(
                blockHash,
                new Driver.BlockCallback() {
                    @Override
                    public void onResponse(int status, String message, Block block) {
                        if (status == STATUS.OK) {
                            System.out.println("==> Block: " + block.toString());
                            callback.onResponse(status, message, block);
                        } else {
                            System.out.println(message);
                            callback.onResponse(status, message, block);
                        }
                    }
                });
    }

    @Override
    public void getBlockByNumber(
            String chainPath, long blockNumber, Driver.BlockCallback callback) {
        Driver driver = drivers.get(Utils.getChainPath(chainPath));
        driver.getBlockByNumber(
                blockNumber,
                new Driver.BlockCallback() {
                    @Override
                    public void onResponse(int status, String message, Block block) {
                        if (status == STATUS.OK) {
                            System.out.println("==> Block: " + block.toString());
                            callback.onResponse(status, message, block);
                        } else {
                            System.out.println(message);
                            callback.onResponse(status, message, block);
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
    public void listResources(String chainPath, Driver.ResourcesCallback callback) {
        Driver driver = drivers.get(Utils.getChainPath(chainPath));
        driver.listResources(
                new Driver.ResourcesCallback() {
                    @Override
                    public void onResponse(int status, String message, Resource[] resources) {
                        if (status == STATUS.OK) {
                            System.out.println("==> Resources: size " + resources.length);
                            callback.onResponse(status, message, resources);
                        } else {
                            System.out.println(message);
                            callback.onResponse(status, message, resources);
                        }
                    }
                });
    }
}
