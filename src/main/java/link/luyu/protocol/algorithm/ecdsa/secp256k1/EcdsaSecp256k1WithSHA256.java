package link.luyu.protocol.algorithm.ecdsa.secp256k1;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.ECGenParameterSpec;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import link.luyu.protocol.algorithm.SignatureAlgorithm;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9IntegerConverter;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.signers.HMacDSAKCalculator;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jcajce.provider.digest.Keccak;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;
import org.bouncycastle.math.ec.custom.sec.SecP256K1Curve;
import org.bouncycastle.util.encoders.Hex;

public class EcdsaSecp256k1WithSHA256 implements SignatureAlgorithm {
    public static final String TYPE = "ECDSA_SECP256K1_WITH_SHA256";

    public static final X9ECParameters CURVE_PARAMS = CustomNamedCurves.getByName("secp256k1");
    public static final ECDomainParameters CURVE =
            new ECDomainParameters(
                    CURVE_PARAMS.getCurve(),
                    CURVE_PARAMS.getG(),
                    CURVE_PARAMS.getN(),
                    CURVE_PARAMS.getH());

    public static final SecureRandom SECURE_RANDOM = new SecureRandom();

    {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public byte[] sign(byte[] secKey, byte[] message) {

        // hash message
        byte[] hashedMessage = sha256(message);

        // sign message
        BigInteger intSecKey = new BigInteger(1, secKey);
        ECDSASigner signer = new ECDSASigner(new HMacDSAKCalculator(new SHA256Digest()));

        ECPrivateKeyParameters privKey = new ECPrivateKeyParameters(intSecKey, CURVE);
        signer.init(true, privKey);
        byte[] signBytes = signer.generateSignatureVRS(hashedMessage);

        return signBytes;
    }

    @Override
    public boolean verify(byte[] pubKey, byte[] signBytes, byte[] message) {
        byte[] recoverPubKey = recover(message, signBytes);
        BigInteger publicKeyValue = new BigInteger(1, pubKey);
        BigInteger recoverPubKeyValue = new BigInteger(1, recoverPubKey);

        return publicKeyValue.equals(recoverPubKeyValue);
    }

    @Override
    public Map.Entry<byte[], byte[]> generateKeyPair() {
        try {
            KeyPair keyPair = createSecp256k1KeyPair(SECURE_RANDOM);

            BCECPrivateKey privateKey = (BCECPrivateKey) keyPair.getPrivate();
            BCECPublicKey publicKey = (BCECPublicKey) keyPair.getPublic();

            BigInteger privateKeyValue = privateKey.getD();

            // Ethereum does not use encoded public keys like bitcoin - see
            // https://en.bitcoin.it/wiki/Elliptic_Curve_Digital_Signature_Algorithm for details
            // Additionally, as the first bit is a constant prefix (0x04) we ignore this value
            byte[] encoded = publicKey.getQ().getEncoded(false);
            byte[] publicKeyBytes = Arrays.copyOfRange(encoded, 1, encoded.length);

            Map.Entry<byte[], byte[]> keyPairBytes =
                    new AbstractMap.SimpleEntry<>(publicKeyBytes, privateKeyValue.toByteArray());
            return keyPairBytes;

        } catch (Exception e) {
            throw new RuntimeException("generate " + TYPE + " key pair failed, " + e);
        }
    }

    public static byte[] recover(byte[] message, byte[] signBytes) {
        // hash message
        byte[] hashedMessage = sha256(message);

        SignatureData signatureData = SignatureData.parseFrom(signBytes);

        int recId = (int) signatureData.getV() - 27;
        BigInteger r = signatureData.getR();
        BigInteger s = signatureData.getS();

        // 1.0 For j from 0 to h   (h == recId here and the loop is outside this function)
        //   1.1 Let x = r + jn
        BigInteger n = CURVE.getN(); // Curve order.
        BigInteger i = BigInteger.valueOf((long) recId / 2);
        BigInteger x = r.add(i.multiply(n));

        // More concisely, what these points mean is to use X as a compressed public key.
        BigInteger prime = SecP256K1Curve.q;
        if (x.compareTo(prime) >= 0) {
            // Cannot have point co-ordinates larger than this as everything takes place modulo Q.
            return null;
        }
        // Compressed keys require you to know an extra bit of data about the y-coord as there are
        // two possibilities. So it's encoded in the recId.
        ECPoint R = decompressKey(x, (recId & 1) == 1);
        //   1.4. If nR != point at infinity, then do another iteration of Step 1 (callers
        //        responsibility).
        if (!R.multiply(n).isInfinity()) {
            return null;
        }
        //   1.5. Compute e from M using Steps 2 and 3 of ECDSA signature verification.
        BigInteger e = new BigInteger(1, hashedMessage);
        //   1.6. For k from 1 to 2 do the following.   (loop is outside this function via
        //        iterating recId)
        //   1.6.1. Compute a candidate public key as:
        //               Q = mi(r) * (sR - eG)
        //
        // Where mi(x) is the modular multiplicative inverse. We transform this into the following:
        //               Q = (mi(r) * s ** R) + (mi(r) * -e ** G)
        // Where -e is the modular additive inverse of e, that is z such that z + e = 0 (mod n).
        // In the above equation ** is point multiplication and + is point addition (the EC group
        // operator).
        //
        // We can find the additive inverse by subtracting e from zero then taking the mod. For
        // example the additive inverse of 3 modulo 11 is 8 because 3 + 8 mod 11 = 0, and
        // -3 mod 11 = 8.
        BigInteger eInv = BigInteger.ZERO.subtract(e).mod(n);
        BigInteger rInv = r.modInverse(n);
        BigInteger srInv = rInv.multiply(s).mod(n);
        BigInteger eInvrInv = rInv.multiply(eInv).mod(n);
        ECPoint q = ECAlgorithms.sumOfTwoMultiplies(CURVE.getG(), eInvrInv, R, srInv);

        byte[] qBytes = q.getEncoded(false);
        // We remove the prefix
        return new BigInteger(1, Arrays.copyOfRange(qBytes, 1, qBytes.length)).toByteArray();
    }

    /** Decompress a compressed public key (x co-ord and low-bit of y-coord). */
    private static ECPoint decompressKey(BigInteger xBN, boolean yBit) {
        X9IntegerConverter x9 = new X9IntegerConverter();
        byte[] compEnc = x9.integerToBytes(xBN, 1 + x9.getByteLength(CURVE.getCurve()));
        compEnc[0] = (byte) (yBit ? 0x03 : 0x02);
        return CURVE.getCurve().decodePoint(compEnc);
    }

    public static byte[] sha256(byte[] input) {
        Keccak.DigestKeccak kecc = new Keccak.Digest256();
        kecc.update(input, 0, input.length);
        return kecc.digest();
    }

    public static String getAddress(byte[] publicKey) {
        byte[] hash = sha256(publicKey);
        return "0x" + Hex.toHexString(hash, hash.length - 20, 20);
    }

    public static byte[] secKey2PubKey(byte[] secKey) {
        BigInteger secKeyInt = new BigInteger(1, secKey);
        // Notice: not to use publicKeyFromSecretKey(secKeyInt).toByteArray() to get pubBytes
        // Because in some cases, there are extra prefix 00 with length 65 in
        // pubKeyInt.toByteArray(), so we may not to use this to get pubKey's bytes
        ECPoint point = publicPointFromPrivate(secKeyInt);
        byte[] encoded = point.getEncoded(false);

        byte[] publicKeyBytes = Arrays.copyOfRange(encoded, 1, encoded.length);
        return publicKeyBytes;
    }

    /**
     * Returns public key from the given secret key.
     *
     * @param secKey the private key to derive the public key from
     * @return BigInteger encoded public key
     */
    private static BigInteger publicKeyFromSecretKey(BigInteger secKey) {
        ECPoint point = publicPointFromPrivate(secKey);

        byte[] encoded = point.getEncoded(false);
        return new BigInteger(1, Arrays.copyOfRange(encoded, 1, encoded.length)); // remove prefix
    }

    /**
     * Returns public key point from the given private key.
     *
     * @param secKey the private key to derive the public key from
     * @return ECPoint public key
     */
    private static ECPoint publicPointFromPrivate(BigInteger secKey) {
        /*
         * TODO: FixedPointCombMultiplier currently doesn't support scalars longer than the group
         * order, but that could change in future versions.
         */
        if (secKey.bitLength() > CURVE.getN().bitLength()) {
            secKey = secKey.mod(CURVE.getN());
        }
        return new FixedPointCombMultiplier().multiply(CURVE.getG(), secKey);
    }

    private static KeyPair createSecp256k1KeyPair(SecureRandom random)
            throws NoSuchProviderException, NoSuchAlgorithmException,
                    InvalidAlgorithmParameterException {

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ECDSA", "BC");
        ECGenParameterSpec ecGenParameterSpec = new ECGenParameterSpec("secp256k1");
        if (random != null) {
            keyPairGenerator.initialize(ecGenParameterSpec, random);
        } else {
            keyPairGenerator.initialize(ecGenParameterSpec);
        }
        return keyPairGenerator.generateKeyPair();
    }

    public static byte[] generateKeyPairPKCS8Encoded() {
        try {
            Security.addProvider(new BouncyCastleProvider());

            KeyPair keyPair = createSecp256k1KeyPair(SECURE_RANDOM);

            BCECPrivateKey privateKey = (BCECPrivateKey) keyPair.getPrivate();

            return privateKey.getEncoded();

        } catch (Exception e) {
            throw new RuntimeException("generate " + TYPE + " key pair failed, " + e);
        }
    }
}
