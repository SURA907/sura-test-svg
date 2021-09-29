package link.luyu.protocol.algorithm.ecdsa.secp256r1;

import static java.lang.String.format;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.spec.ECGenParameterSpec;
import java.util.AbstractMap;
import java.util.Map;
import javax.xml.bind.DatatypeConverter;
import link.luyu.protocol.algorithm.SignatureAlgorithm;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequenceGenerator;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECPrivateKeySpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.ECPoint;

public class EcdsaSecp256r1WithSHA256 implements SignatureAlgorithm {
    public static final String TYPE = "ECDSA_SECP256R1_WITH_SHA256";

    public static final String DEFAULT_SIGNATURE_ALGORITHM = "SHA256WithECDSA";
    public static final String PURE_SIGNATURE_ALGORITHM = "ECDSA";
    public static final String CURVE_NAME = "secp256r1";
    private static final ECNamedCurveParameterSpec CURVE_PARAM_SPEC =
            ECNamedCurveTable.getParameterSpec(CURVE_NAME);
    public static final BigInteger curveN = CURVE_PARAM_SPEC.getN();
    public static final String PROVIDER_NAME = "BC";

    public static final SecureRandom SECURE_RANDOM = new SecureRandom();

    {
        Security.addProvider(new BouncyCastleProvider());
    }

    private Signature getSigner() throws Exception {
        Signature sig = Signature.getInstance(DEFAULT_SIGNATURE_ALGORITHM);
        return sig;
    }

    private PrivateKey parseSecKeyBytes(byte[] secBytes) throws Exception {
        KeyFactory factory = KeyFactory.getInstance(PURE_SIGNATURE_ALGORITHM, PROVIDER_NAME);
        ECPrivateKeySpec ecPrivateKeySpec =
                new ECPrivateKeySpec(new BigInteger(1, secBytes), CURVE_PARAM_SPEC);
        PrivateKey privateKey = factory.generatePrivate(ecPrivateKeySpec);
        return privateKey;
    }

    private PublicKey parsePubKeyBytes(byte[] pubBytes) throws Exception {
        KeyFactory factory = KeyFactory.getInstance(PURE_SIGNATURE_ALGORITHM, PROVIDER_NAME);
        ECPoint pubPoint = CURVE_PARAM_SPEC.getCurve().decodePoint(pubBytes);
        ECPublicKeySpec ecPublicKeySpec = new ECPublicKeySpec(pubPoint, CURVE_PARAM_SPEC);
        PublicKey publicKey = factory.generatePublic(ecPublicKeySpec);
        return publicKey;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public byte[] sign(byte[] secKey, byte[] message) {
        if (message == null) {
            throw new RuntimeException("Data that to be signed is null.");
        }
        if (message.length == 0) {
            throw new RuntimeException("Data to be signed was empty.");
        }

        try {
            PrivateKey privateKey = parseSecKeyBytes(secKey);

            Signature sig = getSigner();
            sig.initSign(privateKey);
            sig.update(message);
            byte[] signature = sig.sign();

            BigInteger[] sigs = decodeECDSASignature(signature);

            sigs = preventMalleability(sigs, curveN);

            try (ByteArrayOutputStream s = new ByteArrayOutputStream()) {

                DERSequenceGenerator seq = new DERSequenceGenerator(s);
                seq.addObject(new ASN1Integer(sigs[0]));
                seq.addObject(new ASN1Integer(sigs[1]));
                seq.close();
                return s.toByteArray();
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not sign the message using private key", e);
        }
    }

    @Override
    public boolean verify(byte[] pubKey, byte[] signBytes, byte[] message) {

        if (pubKey == null || signBytes == null || message == null) {
            return false;
        }

        try {
            PublicKey publicKey = parsePubKeyBytes(pubKey);

            Signature sig = getSigner();
            sig.initVerify(publicKey);
            sig.update(message);
            boolean ok = sig.verify(signBytes);
            return ok;
        } catch (Exception e) {
            throw new RuntimeException("Could not verify the message using public key", e);
        }
    }

    @Override
    public Map.Entry<byte[], byte[]> generateKeyPair() {

        try {
            KeyPair keyPair = createSecp256r1KeyPair();

            BCECPrivateKey privateKey = (BCECPrivateKey) keyPair.getPrivate();
            BCECPublicKey publicKey = (BCECPublicKey) keyPair.getPublic();

            BigInteger privateKeyValue = privateKey.getD();

            byte[] publicKeyBytes = publicKey.getQ().getEncoded(false);

            // Notice that we remain '04' in publicKeyBytes starting for easily coding,
            // this is different with what we do in secp256k1 in eth
            Map.Entry<byte[], byte[]> keyPairBytes =
                    new AbstractMap.SimpleEntry<>(publicKeyBytes, privateKeyValue.toByteArray());
            return keyPairBytes;

        } catch (Exception e) {
            throw new RuntimeException("generate " + TYPE + " key pair failed, " + e);
        }
    }

    public static KeyPair createSecp256r1KeyPair()
            throws NoSuchProviderException, NoSuchAlgorithmException,
                    InvalidAlgorithmParameterException {

        KeyPairGenerator keyPairGenerator =
                KeyPairGenerator.getInstance(PURE_SIGNATURE_ALGORITHM, PROVIDER_NAME);
        ECGenParameterSpec ecGenParameterSpec = new ECGenParameterSpec(CURVE_NAME);

        if (SECURE_RANDOM != null) {
            keyPairGenerator.initialize(ecGenParameterSpec, SECURE_RANDOM);
        } else {
            keyPairGenerator.initialize(ecGenParameterSpec);
        }
        return keyPairGenerator.generateKeyPair();
    }

    private static BigInteger[] decodeECDSASignature(byte[] signature) throws Exception {

        try (ByteArrayInputStream inStream = new ByteArrayInputStream(signature)) {
            ASN1InputStream asnInputStream = new ASN1InputStream(inStream);
            ASN1Primitive asn1 = asnInputStream.readObject();

            BigInteger[] sigs = new BigInteger[2];
            int count = 0;
            if (asn1 instanceof ASN1Sequence) {
                ASN1Sequence asn1Sequence = (ASN1Sequence) asn1;
                ASN1Encodable[] asn1Encodables = asn1Sequence.toArray();
                for (ASN1Encodable asn1Encodable : asn1Encodables) {
                    ASN1Primitive asn1Primitive = asn1Encodable.toASN1Primitive();
                    if (asn1Primitive instanceof ASN1Integer) {
                        ASN1Integer asn1Integer = (ASN1Integer) asn1Primitive;
                        BigInteger integer = asn1Integer.getValue();
                        if (count < 2) {
                            sigs[count] = integer;
                        }
                        count++;
                    }
                }
            }
            if (count != 2) {
                throw new CryptoException(
                        format(
                                "Invalid ECDSA signature. Expected count of 2 but got: %d. Signature is: %s",
                                count, DatatypeConverter.printHexBinary(signature)));
            }
            return sigs;
        }
    }

    private static BigInteger[] preventMalleability(BigInteger[] sigs, BigInteger curveN) {
        BigInteger cmpVal = curveN.divide(BigInteger.valueOf(2L));

        BigInteger sval = sigs[1];

        if (sval.compareTo(cmpVal) == 1) {

            sigs[1] = curveN.subtract(sval);
        }

        return sigs;
    }
}
