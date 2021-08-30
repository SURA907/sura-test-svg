package link.luyu.protocol.algorithm.sm2;

import java.math.BigInteger;
import java.util.Arrays;

public class SignatureData {
    public static final int SM2_PRIVATE_KEY_SIZE = 32;

    private byte v;
    private BigInteger r;
    private BigInteger s;

    public SignatureData(byte v, BigInteger r, BigInteger s) {
        this.v = v;
        this.r = r;
        this.s = s;
    }

    public byte getV() {
        return v;
    }

    public BigInteger getR() {
        return r;
    }

    public BigInteger getS() {
        return s;
    }

    public static SignatureData parseFrom(byte[] bytes) {
        if (bytes.length != 65) {
            throw new RuntimeException(
                    "Illegal signature bytes length: "
                            + bytes.length
                            + " "
                            + Arrays.toString(bytes));
        }

        BigInteger r = new BigInteger(1, Arrays.copyOfRange(bytes, 1, 1 + SM2_PRIVATE_KEY_SIZE));
        BigInteger s =
                new BigInteger(
                        1,
                        Arrays.copyOfRange(
                                bytes, SM2_PRIVATE_KEY_SIZE + 1, SM2_PRIVATE_KEY_SIZE * 2 + 1));

        return new SignatureData(bytes[0], r, s);
    }

    public byte[] toBytes() {
        byte[] ans = new byte[SM2_PRIVATE_KEY_SIZE * 2 + 1];
        ans[0] = this.v;

        byte[] bytes = this.r.toByteArray();
        int offset = 0;
        int leadingZeros = SM2_PRIVATE_KEY_SIZE - bytes.length;
        if (bytes[0] == 0) {
            offset = 1;
            leadingZeros += 1;
        }
        if (leadingZeros < 0) {
            throw new RuntimeException(
                    "Input r is too large to put in byte array of size " + bytes.length);
        }
        System.arraycopy(bytes, offset, ans, 1 + leadingZeros, bytes.length - offset);

        bytes = this.s.toByteArray();
        if (bytes[0] == 0) {
            offset = 1;
            leadingZeros = SM2_PRIVATE_KEY_SIZE - bytes.length + 1;
        } else {
            offset = 0;
            leadingZeros = SM2_PRIVATE_KEY_SIZE - bytes.length;
        }
        if (leadingZeros < 0) {
            throw new RuntimeException(
                    "Input s is too large to put in byte array of size " + bytes.length);
        }
        System.arraycopy(
                bytes, offset, ans, 1 + SM2_PRIVATE_KEY_SIZE + leadingZeros, bytes.length - offset);

        return ans;
    }

    /**
     * @return true if the S component is "low". See <a
     *     href="https://github.com/bitcoin/bips/blob/master/bip-0062.mediawiki#Low_S_values_in_signatures">
     *     BIP62</a>.
     */
    public boolean isCanonical() {
        return s.compareTo(SM2WithSM3.HALF_CURVE_ORDER) <= 0;
    }

    /**
     * Will automatically adjust the S component to be less than or equal to half the curve order,
     * if necessary. This is required because for every signature (r,s) the signature (r, -s (mod
     * N)) is a valid signature of the same message. However, we dislike the ability to modify the
     * bits of a Bitcoin transaction after it's been signed, as that violates various assumed
     * invariants. Thus in future only one of those forms will be considered legal and the other
     * will be banned.
     *
     * @return the signature in a canonicalised form.
     */
    public SignatureData toCanonicalised() {
        if (!isCanonical()) {
            // The order of the curve is the number of valid points that exist on that curve.
            // If S is in the upper half of the number of valid points, then bring it back to
            // the lower half. Otherwise, imagine that
            //    N = 10
            //    s = 8, so (-8 % 10 == 2) thus both (r, 8) and (r, 2) are valid solutions.
            //    10 - 8 == 2, giving us always the latter solution, which is canonical.
            return new SignatureData(v, r, SM2WithSM3.CURVE.getN().subtract(s));
        } else {
            return this;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SignatureData that = (SignatureData) o;

        if (v != that.v) {
            return false;
        }
        if (!r.equals(that.r)) {
            return false;
        }
        return s.equals(that.s);
    }

    @Override
    public int hashCode() {
        int result = v;
        result = 31 * result + r.hashCode();
        result = 31 * result + s.hashCode();
        return result;
    }
}
