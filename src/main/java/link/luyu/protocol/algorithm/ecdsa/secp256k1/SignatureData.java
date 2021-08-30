package link.luyu.protocol.algorithm.ecdsa.secp256k1;

import java.math.BigInteger;
import java.util.Arrays;

public class SignatureData {
    private static final int ECDSA_PRIVATE_KEY_SIZE = 32;

    private int v;
    private BigInteger r;
    private BigInteger s;

    public SignatureData(int v, BigInteger r, BigInteger s) {
        this.v = v;
        this.r = r;
        this.s = s;
    }

    public static SignatureData parseFrom(byte[] bytes) {
        if (bytes.length != 65) {
            throw new RuntimeException(
                    "Illegal signature bytes length: "
                            + bytes.length
                            + " "
                            + Arrays.toString(bytes));
        }

        int v = (int) bytes[0];
        BigInteger r =
                Utils.toBigIntFromPadded(Arrays.copyOfRange(bytes, 1, ECDSA_PRIVATE_KEY_SIZE + 1));
        BigInteger s =
                Utils.toBigIntFromPadded(
                        Arrays.copyOfRange(
                                bytes, ECDSA_PRIVATE_KEY_SIZE + 1, 2 * ECDSA_PRIVATE_KEY_SIZE + 1));

        return new SignatureData(v, r, s);
    }

    public byte[] toBytes() {
        byte vByte = (byte) v;
        byte[] rBytes = Utils.toBytesPadded(r, ECDSA_PRIVATE_KEY_SIZE);
        byte[] sBytes = Utils.toBytesPadded(s, ECDSA_PRIVATE_KEY_SIZE);

        byte[] bytes = new byte[2 * ECDSA_PRIVATE_KEY_SIZE + 1]; // 1(v) + 32(r) + 32(s)
        bytes[0] = vByte;
        System.arraycopy(rBytes, 0, bytes, 1, ECDSA_PRIVATE_KEY_SIZE);
        System.arraycopy(sBytes, 0, bytes, ECDSA_PRIVATE_KEY_SIZE + 1, ECDSA_PRIVATE_KEY_SIZE);
        return bytes;
    }

    public int getV() {
        return v;
    }

    public BigInteger getR() {
        return r;
    }

    public BigInteger getS() {
        return s;
    }
}
