package org.luyu.protocol.algorithm.sm2;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Assert;
import org.junit.Test;
import org.luyu.protocol.algorithm.SignatureAlgorithm;

public class SM2WithSM3Test {
    @Test
    public void SM2WithSM3Test1() {
        byte[] message = "test".getBytes(StandardCharsets.UTF_8);

        java.util.Random ran = new java.util.Random();
        byte[] secKey = new byte[SignatureData.SM2_PRIVATE_KEY_SIZE];
        ran.nextBytes(secKey);
        BigInteger privateKey = new BigInteger(1, secKey);
        BigInteger publicKey = SM2WithSM3.publicKeyFromPrivate(privateKey);
        byte[] pubKey = publicKey.toByteArray();

        SignatureAlgorithm signer = new SM2WithSM3();
        byte[] signature = signer.sign(secKey, message);
        boolean ok = signer.verify(pubKey, signature, message);

        System.out.println("res: " + ok);

        Assert.assertTrue("should be true", ok);
    }

    @Test
    public void SM2WithSM3Test2() {
        byte[] secKey =
                Hex.decode("58967e2beb6fffd3c96545eebd3000b39c10087d48faa0d41f9c7bf3720e0ea4");
        byte[] pubKey =
                Hex.decode(
                        "ec7e40b8dfa4b14383f703ec5403b71db0ab505b9fc41f0df45a9910a307dfbd5b3c5afdd4b90d79fa0ab70d53fd88422df77e09b254a53e72b4857f74ab1da4");
        BigInteger privateKey = new BigInteger(1, secKey);
        BigInteger publicKey = SM2WithSM3.publicKeyFromPrivate(privateKey);
        // this pubkey has a leading zero

        byte[] pubKey1 = publicKey.toByteArray();
        pubKey1 = Arrays.copyOfRange(pubKey1, 1, pubKey1.length);
        Assert.assertTrue(java.util.Arrays.equals(pubKey, pubKey1));
    }

    @Test
    public void genrateKeysTest() {
        SignatureAlgorithm signer = new SM2WithSM3();
        Map.Entry<byte[], byte[]> key = signer.generateKeyPair();
        BigInteger privateKey = new BigInteger(1, key.getKey());
        // System.out.println("1 privateKey: " + privateKey);
        BigInteger publicKey = SM2WithSM3.publicKeyFromPrivate(privateKey);
        BigInteger publicKey1 = new BigInteger(1, key.getValue());
        // System.out.println("publicKey: " + publicKey);
        // System.out.println("publicKey1: " + publicKey1);
        Assert.assertTrue("should be true", publicKey.equals(publicKey1));
    }
}
