package org.luyu.protocol.application;

import org.junit.Assert;
import org.junit.Test;
import org.luyu.protocol.network.MockRouter;
import org.luyu.protocol.network.Receipt;
import org.luyu.protocol.network.RouterManager;
import org.luyu.protocol.network.Transaction;

public class AllTest {
    private MockSDK sdk = new MockSDK();

    public AllTest() {}

    @Test
    public void sendTransactionTest() throws Exception {
        RouterManager router = new MockRouter();
        sdk.connectRouter(router);

        Transaction tx = new Transaction();
        tx.setPath("payment.chain0.hello");
        tx.setMethod("set");
        tx.setArgs(new String[] {"aaaaa"});
        tx.setNonce(123456);
        tx.setLuyuSign(new byte[] {});

        for (int i = 0; i < 20; i++) {
            Thread.sleep(1700);
            Receipt receipt = sdk.sendTransaction(tx);
            Assert.assertNotNull(receipt);
        }
    }
}
