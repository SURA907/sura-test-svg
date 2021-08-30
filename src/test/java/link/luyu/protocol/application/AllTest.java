package link.luyu.protocol.application;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import link.luyu.protocol.network.Block;
import link.luyu.protocol.network.CallRequest;
import link.luyu.protocol.network.CallResponse;
import link.luyu.protocol.network.MockRouter;
import link.luyu.protocol.network.Receipt;
import link.luyu.protocol.network.Resource;
import link.luyu.protocol.network.RouterManager;
import link.luyu.protocol.network.Transaction;
import org.junit.Assert;
import org.junit.Test;

public class AllTest {
    private static final String RESOURCE_PATH = "payment.chain0.hello";
    private MockSDK sdk = new MockSDK();

    private ExecutorService executor = Executors.newFixedThreadPool(20);
    private SecureRandom rand = new SecureRandom();

    public AllTest() {
        RouterManager router = new MockRouter();
        sdk.connectRouter(router);
    }

    @Test
    public void sendTransactionTest() throws Exception {
        Thread.sleep(5000);
        for (int i = 0; i < 20; i++) {
            Thread.sleep(1700);
            int txNum = rand.nextInt(10);
            for (int j = 0; j < txNum; j++) {
                executor.submit(
                        () -> {
                            Transaction tx = new Transaction();
                            tx.setPath(RESOURCE_PATH);
                            tx.setMethod("set");
                            tx.setArgs(new String[] {"aaaaa"});
                            tx.setNonce(rand.nextLong());
                            tx.setLuyuSign(new byte[] {});
                            Receipt receipt = sdk.sendTransaction(tx).send();
                            Assert.assertNotNull(receipt);
                        });
            }
        }
        executor.awaitTermination(10, TimeUnit.SECONDS);
    }

    @Test
    public void callTest() throws Exception {
        Thread.sleep(5000);
        for (int i = 0; i < 20; i++) {
            Thread.sleep(1700);
            int txNum = rand.nextInt(10);
            for (int j = 0; j < txNum; j++) {
                executor.submit(
                        () -> {
                            CallRequest callRequest = new CallRequest();
                            callRequest.setPath(RESOURCE_PATH);
                            callRequest.setMethod("set");
                            callRequest.setArgs(new String[] {"aaaaa"});
                            CallResponse response = sdk.call(callRequest).send();
                            Assert.assertNotNull(response);
                        });
            }
        }
        executor.awaitTermination(10, TimeUnit.SECONDS);
    }

    @Test
    public void getTransactionReceiptTest() throws Exception {
        Thread.sleep(5000);
        for (int i = 0; i < 20; i++) {
            Thread.sleep(1700);
            int txNum = rand.nextInt(10);
            for (int j = 0; j < txNum; j++) {
                final String txHash = "0xhash--" + i + "--" + j;
                executor.submit(
                        () -> {
                            Receipt receipt =
                                    sdk.getTransactionReceipt(RESOURCE_PATH, txHash).send();
                            Assert.assertNotNull(receipt);
                        });
            }
        }
        executor.awaitTermination(10, TimeUnit.SECONDS);
    }

    @Test
    public void getBlockAndNumberTest() throws Exception {
        Thread.sleep(5000);
        long testBlockNumber = 0;
        while (testBlockNumber <= sdk.getBlockNumber(RESOURCE_PATH).send()) {
            Thread.sleep(1700);
            Block blockX = sdk.getBlockByNumber(RESOURCE_PATH, testBlockNumber).send();
            Block blockY =
                    sdk.getBlockByHash(RESOURCE_PATH, String.valueOf(testBlockNumber)).send();
            if (blockX == null || blockY == null) {
                continue;
            }

            Assert.assertEquals(blockX, blockY);
            testBlockNumber = blockX.getNumber() + 1;
        }
    }

    @Test
    public void listResourcesTest() throws Exception {
        Thread.sleep(5000);
        for (int i = 0; i < 10; i++) {
            Thread.sleep(1700);
            Resource[] resources = sdk.listResources(RESOURCE_PATH).send();
            Assert.assertNotNull(resources);
            System.out.println(Arrays.toString(resources));
        }
    }

    public void allTest() throws Exception {
        executor.submit(
                () -> {
                    try {
                        sendTransactionTest();
                    } catch (Exception e) {
                        System.out.println();
                    }
                });

        executor.submit(
                () -> {
                    try {
                        callTest();
                    } catch (Exception e) {
                        System.out.println();
                    }
                });

        executor.submit(
                () -> {
                    try {
                        getTransactionReceiptTest();
                    } catch (Exception e) {
                        System.out.println();
                    }
                });

        executor.submit(
                () -> {
                    try {
                        getBlockAndNumberTest();
                    } catch (Exception e) {
                        System.out.println();
                    }
                });

        executor.submit(
                () -> {
                    try {
                        listResourcesTest();
                    } catch (Exception e) {
                        System.out.println();
                    }
                });

        executor.awaitTermination(300, TimeUnit.SECONDS);
    }
}
