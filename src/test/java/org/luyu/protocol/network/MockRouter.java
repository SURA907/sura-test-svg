package org.luyu.protocol.network;

import java.util.HashMap;
import java.util.Map;
import org.luyu.protocol.link.Connection;
import org.luyu.protocol.link.Driver;
import org.luyu.protocol.link.HelloPluginBuilder;

public class MockRouter implements RouterManager {

    private Driver driver;
    private Connection connection;
    private AccountManager accountManager = new MockAccountManager();

    public MockRouter() {

        HelloPluginBuilder builder = new HelloPluginBuilder();

        // Build connection
        Map<String, Object> connectionConfig = new HashMap<>();
        connectionConfig.put("chains", new String[] {"payment.chain0"});
        this.connection = builder.newConnection(connectionConfig);

        // Build driver
        Map<String, Object> driverConfig = new HashMap<>();
        this.driver = builder.newDriver(this.connection, driverConfig);

        // start connection
        this.connection.start();
    }

    @Override
    public void sendTransaction(Transaction tx, ReceiptCallback callback) {
        ChainAccount chainAccount = accountManager.verifyAndGetChainAccount(tx);
        if (chainAccount != null) {
            tx.setKey(chainAccount.getSecKey());

            System.out.println("==> SendTransaction: " + tx.toString());
            driver.sendTransaction(
                    tx,
                    new Driver.ReceiptCallback() {
                        @Override
                        public void onResponse(Receipt receipt) {
                            System.out.println("==> Receipt: " + receipt.toString());
                            callback.onResponse(receipt);
                        }

                        @Override
                        public void onFailed(Throwable e) {
                            System.out.println(e.getMessage());
                        }
                    });
        }
    }

    @Override
    public void call(CallRequest request, CallResponseCallback callback) {}

    @Override
    public void getTransactionReceipt(String txHash, ReceiptCallback callback) {}

    @Override
    public void getBlockByHash(String blockHash, BlockCallback callback) {}

    @Override
    public void getBlockByNumber(long blockNumber, BlockCallback callback) {}

    @Override
    public void listResources(String chainPath, Driver.ResourcesCallback callback) {}
}
