package org.luyu.protocol.application;

import org.luyu.protocol.network.Block;
import org.luyu.protocol.network.CallRequest;
import org.luyu.protocol.network.CallResponse;
import org.luyu.protocol.network.Receipt;
import org.luyu.protocol.network.Resource;
import org.luyu.protocol.network.Transaction;

public interface SDK {
    RemoteCall<Receipt> sendTransaction(Transaction tx);

    RemoteCall<CallResponse> call(CallRequest request);

    RemoteCall<Receipt> getTransactionReceipt(String chainPath, String txHash);

    RemoteCall<Block> getBlockByHash(String chainPath, String blockHash);

    RemoteCall<Block> getBlockByNumber(String chainPath, long blockNumber);

    RemoteCall<Long> getBlockNumber(String chainPath);

    RemoteCall<Resource[]> listResources(String chainPath);
}
