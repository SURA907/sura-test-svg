package org.luyu.protocol.link;

import org.luyu.protocol.network.CallRequest;
import org.luyu.protocol.network.Events;
import org.luyu.protocol.network.Transaction;

public class HelloDriver implements Driver {

    @Override
    public void sendTransaction(Transaction request, ReceiptCallback callback) {}

    @Override
    public void call(CallRequest request, CallResponseCallback callback) {}

    @Override
    public void getTransactionReceipt(String txHash, ReceiptCallback callback) {}

    @Override
    public void getBlockByHash(String blockHash, BlockCallback callback) {}

    @Override
    public void getBlockByNumber(long blockNumber, BlockCallback callback) {}

    @Override
    public byte[] accountSign(byte[] key, byte[] message) {
        return new byte[0];
    }

    @Override
    public boolean accountVerify(byte[] identity, byte[] signBytes, byte[] message) {
        return false;
    }

    @Override
    public String getType() {
        return "Hello1.0";
    }

    @Override
    public void listResources(String chainPath, ResourcesCallback callback) {}

    @Override
    public void onChainEvent(Events events) {}
}
