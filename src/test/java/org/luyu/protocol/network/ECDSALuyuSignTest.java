package org.luyu.protocol.network;

import org.junit.Assert;
import org.junit.Test;

public class ECDSALuyuSignTest {
    byte[] secKey =
            new byte[] {
                102, -20, 99, 2, -87, 36, -103, 84, -53, -90, -24, 27, 32, -43, 116, 100, -103, -36,
                -120, -53, 74, -38, -75, 27, -128, -24, 70, 88, 89, 61, -44, -81
            };
    byte[] pubKey =
            new byte[] {
                0, -41, -66, -78, 126, -112, -32, -66, 81, -19, -64, 108, 84, 23, 40, 10, -41, -128,
                4, 118, -5, 1, 121, -54, 54, 88, 87, 36, 65, -114, -115, 75, -13, 123, 97, 62, 58,
                -117, 14, -67, 95, 102, 30, 56, -61, 69, -91, -39, 40, -25, -78, -111, 9, 0, 78,
                122, -60, -10, 58, 126, 68, -70, -116, -72, -50
            };

    private LuyuSign signer = new ECDSALuyuSign();
    private LuyuSignData signData;

    public ECDSALuyuSignTest() {
        String sender = signer.secKey2Address(secKey);

        this.signData = new LuyuSignData();
        this.signData.setPath("a.b.c");
        this.signData.setMethod("set");
        this.signData.setArgs(new String[] {"a", "b", "c"});
        this.signData.setNonce(33445566);
        this.signData.setSender(sender);
    }

    @Test
    public void signAndVerify() {
        byte[] signBytes = signer.sign(secKey, signData);
        boolean ok = signer.verify(signBytes, signData);
        Assert.assertTrue(ok);
    }
}
