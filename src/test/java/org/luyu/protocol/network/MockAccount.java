package org.luyu.protocol.network;

import java.nio.charset.StandardCharsets;
import org.luyu.protocol.algorithm.MockSignatureAlgorithm;
import org.luyu.protocol.algorithm.SignatureAlgorithm;

public class MockAccount implements Account {
    @Override
    public byte[] getIdentity() {
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
        return algorithm.verify(getIdentity(), signBytes, message);
    }
}
