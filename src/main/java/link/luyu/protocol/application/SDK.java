package link.luyu.protocol.application;

import link.luyu.protocol.network.Block;
import link.luyu.protocol.network.CallRequest;
import link.luyu.protocol.network.CallResponse;
import link.luyu.protocol.network.Receipt;
import link.luyu.protocol.network.Resource;
import link.luyu.protocol.network.Transaction;

public interface SDK {
    RemoteCall<Receipt> sendTransaction(Transaction tx);

    RemoteCall<CallResponse> call(CallRequest request);

    RemoteCall<Receipt> getTransactionReceipt(String chainPath, String txHash);

    RemoteCall<Block> getBlockByHash(String chainPath, String blockHash);

    RemoteCall<Block> getBlockByNumber(String chainPath, long blockNumber);

    RemoteCall<Long> getBlockNumber(String chainPath);

    RemoteCall<Resource[]> listResources(String chainPath);
}
