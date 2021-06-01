package org.luyu.protocol.algorithm;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.junit.Test;
import org.luyu.protocol.algorithm.ecdsa.secp256k1.EcdsaSecp256k1WithSHA256;

public class SignTest {

    @Test
    public void EcdsaSecp256k1SHA256Test() {
        byte[] message = "test".getBytes(StandardCharsets.UTF_8);
        byte[] secKey =
                new byte[] {
                    102, -20, 99, 2, -87, 36, -103, 84, -53, -90, -24, 27, 32, -43, 116, 100, -103,
                    -36, -120, -53, 74, -38, -75, 27, -128, -24, 70, 88, 89, 61, -44, -81
                };
        byte[] pubKey =
                new byte[] {
                    0, -41, -66, -78, 126, -112, -32, -66, 81, -19, -64, 108, 84, 23, 40, 10, -41,
                    -128, 4, 118, -5, 1, 121, -54, 54, 88, 87, 36, 65, -114, -115, 75, -13, 123, 97,
                    62, 58, -117, 14, -67, 95, 102, 30, 56, -61, 69, -91, -39, 40, -25, -78, -111,
                    9, 0, 78, 122, -60, -10, 58, 126, 68, -70, -116, -72, -50
                };

        SignatureAlgorithm signer = new EcdsaSecp256k1WithSHA256();
        byte[] signBytes = signer.sign(secKey, message);

        boolean ok = signer.verify(pubKey, signBytes, message);

        System.out.println(Arrays.toString(signBytes));
    }
}
