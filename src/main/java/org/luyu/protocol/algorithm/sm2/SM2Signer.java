package org.luyu.protocol.algorithm.sm2;

import java.math.BigInteger;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DSA;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithID;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.signers.DSAKCalculator;
import org.bouncycastle.crypto.signers.RandomDSAKCalculator;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECMultiplier;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;

public class SM2Signer implements ECConstants, DSA {
    private final DSAKCalculator kCalculator;

    private ECDomainParameters ecParams;
    private ECKeyParameters ecKey;

    /** Default configuration, random K values. */
    public SM2Signer() {
        this.kCalculator = new RandomDSAKCalculator();
    }

    /**
     * Configuration with an alternate, possibly deterministic calculator of K.
     *
     * @param kCalculator a K value calculator.
     */
    public SM2Signer(DSAKCalculator kCalculator) {
        this.kCalculator = kCalculator;
    }

    @Override
    public void init(boolean forSigning, CipherParameters param) {
        CipherParameters baseParam;

        if (param instanceof ParametersWithID) {
            throw new IllegalArgumentException("SM2 user ID is unexpected");
        } else {
            baseParam = param;
        }

        if (forSigning) {
            if (baseParam instanceof ParametersWithRandom) {
                ParametersWithRandom rParam = (ParametersWithRandom) baseParam;

                ecKey = (ECKeyParameters) rParam.getParameters();
                ecParams = ecKey.getParameters();
                kCalculator.init(ecParams.getN(), rParam.getRandom());
            } else {
                ecKey = (ECKeyParameters) baseParam;
                ecParams = ecKey.getParameters();
                kCalculator.init(ecParams.getN(), CryptoServicesRegistrar.getSecureRandom());
            }
        } else {
            ecKey = (ECKeyParameters) baseParam;
            ecParams = ecKey.getParameters();
        }
    }

    /** Generate Signature data */
    public SignatureData generateSignatureData(byte[] message) {
        BigInteger n = ecParams.getN();
        BigInteger e = calculateE(n, message);
        BigInteger d = ((ECPrivateKeyParameters) ecKey).getD();

        BigInteger r, s;
        byte v;

        ECMultiplier basePointMultiplier = createBasePointMultiplier();

        // 5.2.1 Draft RFC:  SM2 Public Key Algorithms
        do { // generate s
            BigInteger k;
            do { // generate r
                // A3
                k = kCalculator.nextK();

                // A4
                ECPoint p = basePointMultiplier.multiply(ecParams.getG(), k).normalize();

                // generate v
                v = (byte) (p.getAffineYCoord().testBitZero() ? 1 : 0);

                // A5
                r = e.add(p.getAffineXCoord().toBigInteger()).mod(n);
            } while (r.equals(ZERO) || r.add(k).equals(n));

            // A6
            BigInteger dPlus1ModN = d.add(ONE).modInverse(n);

            s = k.subtract(r.multiply(d)).mod(n);
            s = dPlus1ModN.multiply(s).mod(n);
        } while (s.equals(ZERO));

        // A7
        return new SignatureData(v, r, s);
    }

    @Override
    public BigInteger[] generateSignature(byte[] message) {
        SignatureData sd = generateSignatureData(message);
        return new BigInteger[] {sd.getR(), sd.getS()};
    }

    @Override
    public boolean verifySignature(byte[] message, BigInteger r, BigInteger s) {
        BigInteger n = ecParams.getN();

        // 5.3.1 Draft RFC:  SM2 Public Key Algorithms
        // B1
        if (r.compareTo(ONE) < 0 || r.compareTo(n) >= 0) {
            return false;
        }

        // B2
        if (s.compareTo(ONE) < 0 || s.compareTo(n) >= 0) {
            return false;
        }

        // B4
        BigInteger e = calculateE(n, message);

        // B5
        BigInteger t = r.add(s).mod(n);
        if (t.equals(ZERO)) {
            return false;
        }

        // B6
        ECPoint q = ((ECPublicKeyParameters) ecKey).getQ();
        ECPoint x1y1 = ECAlgorithms.sumOfTwoMultiplies(ecParams.getG(), s, q, t).normalize();
        if (x1y1.isInfinity()) {
            return false;
        }

        // B7
        BigInteger expectedR = e.add(x1y1.getAffineXCoord().toBigInteger()).mod(n);

        return expectedR.equals(r);
    }

    protected ECMultiplier createBasePointMultiplier() {
        return new FixedPointCombMultiplier();
    }

    protected BigInteger calculateE(BigInteger n, byte[] message) {
        return new BigInteger(1, message);
    }
}
