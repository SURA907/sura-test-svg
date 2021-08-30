package link.luyu.protocol.algorithm.sm2;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Arrays;
import java.util.Map;
import link.luyu.protocol.algorithm.SignatureAlgorithm;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9IntegerConverter;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;
import org.bouncycastle.util.encoders.Hex;

public class SM2WithSM3 implements SignatureAlgorithm {
    public static final String TYPE = "SM2_WITH_SM3";

    public static final int PRIVATE_KEY_SIZE = 32;
    public static final int PUBLIC_KEY_SIZE = 64;

    public static final SecureRandom SecureRandomInstance = new SecureRandom();

    public static final X9ECParameters CURVE_PARAMS = CustomNamedCurves.getByName("sm2p256v1");

    static final ECDomainParameters CURVE =
            new ECDomainParameters(
                    CURVE_PARAMS.getCurve(),
                    CURVE_PARAMS.getG(),
                    CURVE_PARAMS.getN(),
                    CURVE_PARAMS.getH());

    static final BigInteger HALF_CURVE_ORDER = CURVE_PARAMS.getN().shiftRight(1);

    @Override
    public String getType() {
        return TYPE;
    }

    /**
     * This function is the internal implementation of SM2 to sign e. If you want to use standard
     * sm2 signature, please use prepareMessage() to calculate e first and use e as this function's
     * sign message
     *
     * @param secKey
     * @param message The message for signing(Is e of standard SM2 signature)
     * @return
     */
    @Override
    public byte[] sign(byte[] secKey, byte[] message) {
        return sign(secKey, message, true).toBytes();
    }

    public SignatureData sign(byte[] secKey, byte[] message, boolean hashFirst) {
        if (hashFirst) {
            message = HashUtil.sm3(message);
        }

        BigInteger secKeyAsBI = new BigInteger(1, secKey);
        SM2Signer signer = new SM2Signer();
        ECPrivateKeyParameters privateKeyParameters =
                new ECPrivateKeyParameters(secKeyAsBI, SM2WithSM3.CURVE);
        signer.init(true, privateKeyParameters);
        return signer.generateSignatureData(message);
    }

    public static byte[] prepareMessage(byte[] pubKey, byte[] message) {
        // use default userID
        byte[] userID = Hex.decode("31323334353637383132333435363738"); // the default value
        return prepareMessage(pubKey, message, userID);
    }

    /**
     * The standard SM2 signature need to calculate [hash(Z) || hash(message)] as e in sign. This
     * function caculate e: [sm3(Z) || sm3(message)] where Z = sm3(entLen || userID || a || b || xg
     * || yg || xa || ya)
     *
     * @param pubKey Account.getPubkey()
     * @param message message before concat with Z
     * @param userID param for calculate Z
     * @return [sm3(Z) || sm3(message)]
     */
    public static byte[] prepareMessage(byte[] pubKey, byte[] message, byte[] userID) {
        try {
            // Z = sm3( entLen || userID || a || b || xg || yg || xa || ya )
            // return sm3(Z) || sm3(message)

            // entLen
            byte[] entLen = new byte[2];
            int len = userID.length * 8;
            entLen[0] = (byte) (len >> 8 & 0xFF);
            entLen[1] = (byte) (len & 0xFF);

            // a b xg yg
            byte[] a = SM2WithSM3.CURVE.getCurve().getA().getEncoded();
            byte[] b = SM2WithSM3.CURVE.getCurve().getB().getEncoded();
            byte[] xg = SM2WithSM3.CURVE.getG().getAffineXCoord().getEncoded();
            byte[] yg = SM2WithSM3.CURVE.getG().getAffineYCoord().getEncoded();

            // xa || ya
            byte[] xaya = pubKey;

            ByteArrayOutputStream encode = new ByteArrayOutputStream();
            encode.write(entLen);
            encode.write(userID);
            encode.write(a);
            encode.write(b);
            encode.write(xg);
            encode.write(yg);
            encode.write(xaya);

            byte[] zHash = HashUtil.sm3(encode.toByteArray());
            byte[] messageHash = HashUtil.sm3(message);

            ByteArrayOutputStream result = new ByteArrayOutputStream();
            result.write(zHash);
            result.write(messageHash);
            return result.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean verify(byte[] pubKey, byte[] signature, byte[] message) {
        BigInteger pubkey1 = new BigInteger(1, pubKey);
        return verify(pubkey1, signature, message, true);
    }

    @Override
    public Map.Entry<byte[], byte[]> generateKeyPair() {
        byte[] privateKey = new byte[PRIVATE_KEY_SIZE];
        SecureRandomInstance.nextBytes(privateKey);

        ECPoint point = publicPointFromPrivate(new BigInteger(1, privateKey)).normalize();
        byte[] publicKey = new byte[PUBLIC_KEY_SIZE];
        byte[] x = point.getXCoord().getEncoded();
        byte[] y = point.getYCoord().getEncoded();
        System.arraycopy(x, 0, publicKey, 0, PUBLIC_KEY_SIZE / 2);
        System.arraycopy(y, 0, publicKey, PUBLIC_KEY_SIZE / 2, PUBLIC_KEY_SIZE / 2);

        return new SimpleImmutableEntry(publicKey, privateKey);
    }

    public boolean verify(BigInteger pubKey, byte[] signature, byte[] message, boolean hashFirst) {
        if (hashFirst) {
            message = HashUtil.sm3(message);
        }

        BigInteger pubKey1 = recover(message, signature);
        return pubKey1.equals(pubKey);
    }

    public static BigInteger recover(byte[] hash, byte[] signature) {
        SignatureData sd = SignatureData.parseFrom(signature);
        byte v = sd.getV();
        BigInteger r = sd.getR();
        BigInteger s = sd.getS();
        if (v < 0) {
            throw new RuntimeException("v must be positive");
        }
        if (r.signum() < 0) {
            throw new RuntimeException("r must be positive");
        }
        if (s.signum() < 0) {
            throw new RuntimeException("s must be positive");
        }
        if (hash == null) {
            throw new RuntimeException("message cannot be null");
        }

        BigInteger n = CURVE.getN();

        if (r.compareTo(ECConstants.ONE) < 0 || r.compareTo(n) >= 0) {
            return null;
        }

        if (s.compareTo(ECConstants.ONE) < 0 || s.compareTo(n) >= 0) {
            return null;
        }

        BigInteger e = new BigInteger(1, hash);

        BigInteger t = r.add(s).mod(n);
        if (t.equals(ECConstants.ZERO)) {
            return null;
        }

        BigInteger rx = r.subtract(e).mod(n);
        ECPoint pt = decompressKey(rx, (v & 1) == 1);
        BigInteger rr = t.modInverse(n);
        BigInteger g = s.multiply(rr).mod(n);
        g = g.negate().mod(n);
        ECPoint q = ECAlgorithms.sumOfTwoMultiplies(CURVE.getG(), g, pt, rr);

        byte[] qBytes = q.getEncoded(false);
        return new BigInteger(1, Arrays.copyOfRange(qBytes, 1, qBytes.length));
    }

    /** Decompress a compressed public key (x co-ord and low-bit of y-coord). */
    private static ECPoint decompressKey(BigInteger xBN, boolean yBit) {
        X9IntegerConverter x9 = new X9IntegerConverter();
        byte[] compEnc = x9.integerToBytes(xBN, 1 + x9.getByteLength(CURVE.getCurve()));
        compEnc[0] = (byte) (yBit ? 0x03 : 0x02);
        return CURVE.getCurve().decodePoint(compEnc);
    }

    /**
     * Returns public key from the given private key.
     *
     * @param privateKey the private key to derive the public key from
     * @return BigInteger encoded public key
     */
    public static BigInteger publicKeyFromPrivate(BigInteger privateKey) {
        ECPoint point = publicPointFromPrivate(privateKey);

        byte[] encoded = point.getEncoded(false);
        return new BigInteger(1, Arrays.copyOfRange(encoded, 1, encoded.length)); // remove prefix
    }

    /**
     * Returns public key point from the given private key.
     *
     * @param privateKey the private key to derive the public key from
     * @return ECPoint public key
     */
    public static ECPoint publicPointFromPrivate(BigInteger privateKey) {
        /*
         * TODO: FixedPointCombMultiplier currently doesn't support scalars longer than the group
         * order, but that could change in future versions.
         */
        if (privateKey.bitLength() > CURVE.getN().bitLength()) {
            privateKey = privateKey.mod(CURVE.getN());
        }
        return new FixedPointCombMultiplier().multiply(CURVE.getG(), privateKey);
    }
}
