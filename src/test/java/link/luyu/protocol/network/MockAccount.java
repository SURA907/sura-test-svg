package link.luyu.protocol.network;

import java.nio.charset.StandardCharsets;
import link.luyu.protocol.algorithm.MockSignatureAlgorithm;
import link.luyu.protocol.algorithm.SignatureAlgorithm;
import link.luyu.protocol.common.STATUS;

public class MockAccount implements Account {
    @Override
    public byte[] getPubKey() {
        return (new String("MockAccount")).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public void sign(byte[] message, SignCallback callback) {
        callback.onResponse(STATUS.OK, "Success", queryAccountManagerToSign(message));
    }

    @Override
    public void verify(byte[] signBytes, byte[] message, VerifyCallback callback) {
        callback.onResponse(STATUS.OK, "Success", queryAccountManagerToVerify(signBytes, message));
    }

    private byte[] queryAccountManagerToSign(byte[] message) {
        SignatureAlgorithm algorithm = new MockSignatureAlgorithm();
        return algorithm.sign(new byte[] {}, message);
    }

    private boolean queryAccountManagerToVerify(byte[] signBytes, byte[] message) {
        SignatureAlgorithm algorithm = new MockSignatureAlgorithm();
        return algorithm.verify(getPubKey(), signBytes, message);
    }
}
