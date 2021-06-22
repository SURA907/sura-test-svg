package org.luyu.protocol.algorithm.ecdsa.secp256k1;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.luyu.protocol.algorithm.SignatureAlgorithm;

public class SignTest {

    @Test
    public void EcdsaSecp256k1SHA256Test() {
        byte[] message = "test".getBytes(StandardCharsets.UTF_8);

        SignatureAlgorithm signer = new EcdsaSecp256k1WithSHA256();
        Map.Entry<byte[], byte[]> keyPair = signer.generateKeyPair();
        byte[] pubKey = keyPair.getKey();
        byte[] secKey = keyPair.getValue();

        byte[] signBytes = signer.sign(secKey, message);

        boolean ok = signer.verify(pubKey, signBytes, message);

        Assert.assertTrue(ok);
        System.out.println(Arrays.toString(signBytes));
    }
}