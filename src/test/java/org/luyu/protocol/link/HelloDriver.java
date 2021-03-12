package org.luyu.protocol.link;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.luyu.protocol.blockchain.MockBlockchain;
import org.luyu.protocol.network.Block;
import org.luyu.protocol.network.CallRequest;
import org.luyu.protocol.network.Events;
import org.luyu.protocol.network.Receipt;
import org.luyu.protocol.network.Transaction;
import org.luyu.protocol.utils.Utils;

public class HelloDriver implements Driver {

    private BlockChainCache blockchain = new BlockChainCache();
    private Connection connection;

    public HelloDriver(Connection connection, Map<String, Object> properties) {
        // Parse config info from properties
        // xxx = propterties.get("aaa")

        // set connection
        this.connection = connection;

        // subscribe event
        subscribeNewBlockEvent(this.connection);
    }

    @Override
    public void sendTransaction(Transaction request, ReceiptCallback callback) {
        // Encode transaction
        String txBytesStr = request.getMethod() + "(";
        for (String arg : request.getArgs()) {
            txBytesStr += arg;
        }
        txBytesStr += ")";
        byte[] txBytes = txBytesStr.getBytes(StandardCharsets.UTF_8);

        // Sign transaction
        byte[] signedTx = accountSign(request.getKey(), txBytes);

        // Send
        connection.asyncSend(
                request.getPath(),
                HelloConnection.SEND_TRANSACTION,
                signedTx,
                new Connection.Callback() {
                    @Override
                    public void onResponse(int errorCode, String message, byte[] responseData) {
                        // assume this demo always response ok

                        // waiting blockHeader syncing
                        String test = new String(responseData);
                        long needBlockNumber = parseBlockNumberFromReceiptAndProof(responseData);
                        while (getBlockNumber() != needBlockNumber) {
                            try {
                                System.out.println("Waiting for block-" + needBlockNumber);
                                Thread.sleep(1000); // You can use Future<> to optimize here
                            } catch (Exception e) {
                            }
                        }

                        // verify transaction on-chain proof
                        if (verifyTransactionAndOnChainProof(
                                blockchain.getHeaderBytes(needBlockNumber), responseData)) {
                            // response receipt
                            Receipt receipt = new Receipt();
                            receipt.setResult(new String(responseData));
                            receipt.setBlockNumber(needBlockNumber);
                            receipt.setMethod(request.getMethod());
                            receipt.setArgs(request.getArgs());
                            receipt.setPath(request.getPath());
                            receipt.setCode(0); // SUCCESS
                            receipt.setMessage("Success");
                            receipt.setTransactionBytes(responseData);
                            receipt.setTransactionHash(new String(responseData));
                            callback.onResponse(receipt);
                        } else {
                            callback.onFailed(
                                    new Exception(
                                            "Proof verify failed of tx: "
                                                    + new String(responseData)));
                        }
                    }
                });
    }

    @Override
    public void call(CallRequest request, CallResponseCallback callback) {}

    @Override
    public void getTransactionReceipt(String txHash, ReceiptCallback callback) {}

    @Override
    public void getBlockByHash(String blockHash, BlockCallback callback) {
        long blockNumber = Long.parseLong(blockHash);
        callback.onResponse(blockchain.getBlock(blockNumber));
    }

    @Override
    public void getBlockByNumber(long blockNumber, BlockCallback callback) {
        callback.onResponse(blockchain.getBlock(blockNumber));
    }

    @Override
    public long getBlockNumber() {
        return blockchain.getBlockNumber();
    }

    @Override
    public byte[] accountSign(byte[] key, byte[] message) {
        return Utils.bytesConcat(message, key);
    }

    @Override
    public boolean accountVerify(byte[] identity, byte[] signBytes, byte[] message) {
        return true;
    }

    @Override
    public String getType() {
        return "Hello1.0";
    }

    @Override
    public void listResources(String chainPath, ResourcesCallback callback) {}

    @Override
    public void registerEvents(Events events) {}

    class BlockChainCache {
        private Map<Long, Block> blockMap = new HashMap<>();
        private long blockNumber = 0;

        public Block getBlock(long number) {
            return blockMap.get(number);
        }

        public void setBlock(Block block) {
            blockMap.put(block.getNumber(), block);
            blockNumber = Long.max(blockNumber, block.getNumber());
        }

        public byte[] getHeaderBytes(long number) {
            return blockMap.get(number).getBytes(); // Use block bytes to simulate in this demo
        }

        public long getBlockNumber() {
            return blockNumber;
        }
    }

    private void subscribeNewBlockEvent(Connection connection) {
        connection.subscribe(
                HelloConnection.EVENT_NEW_BLOCK,
                null,
                new Connection.Callback() {
                    @Override
                    public void onResponse(int errorCode, String message, byte[] responseData) {
                        // responseData is block bytes
                        long blockNumber = MockBlockchain.Block.parseBlockNumber(responseData);

                        // Add block in blockchain cache
                        Block block = new Block();
                        block.setNumber(blockNumber);
                        block.setBytes(responseData);
                        block.setParentHash("aabbccdd");

                        // verify continuity of receive chain
                        String lastBlockHash = "aabbccdd";
                        if (block.getParentHash().equals(lastBlockHash)) {
                            blockchain.setBlock(block);
                            System.out.println(
                                    "Update blockchain cache, blockNumber: " + blockNumber);
                        }
                    }
                });
    }

    private boolean verifyTransactionAndOnChainProof(byte[] blockHeader, byte[] receiptAndProof) {
        return receiptAndProof != null && receiptAndProof.length != 0;
    }

    private long parseBlockNumberFromReceiptAndProof(byte[] receiptAndProof) {
        String str = new String(receiptAndProof);
        long number = Long.parseLong(str.split("-")[0]); // Just simulate the decode process, see
        // MockBlockChain.getTransactionReceipt()
        return number;
    }
}
