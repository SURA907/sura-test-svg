package org.luyu.protocol.application;

import org.luyu.protocol.network.Block;
import org.luyu.protocol.network.CallRequest;
import org.luyu.protocol.network.CallResponse;
import org.luyu.protocol.network.Receipt;
import org.luyu.protocol.network.Resource;
import org.luyu.protocol.network.Transaction;

public interface SDK {
    Receipt sendTransaction(Transaction tx);

    CallResponse call(CallRequest request);

    Receipt getTransactionReceipt(String chainPath, String txHash);

    Block getBlockByHash(String chainPath, String blockHash);

    Block getBlockByNumber(String chainPath, long blockNumber);

    long getBlockNumber(String chainPath);

    Resource[] listResources(String chainPath);
}
