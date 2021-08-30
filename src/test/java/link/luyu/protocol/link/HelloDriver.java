package link.luyu.protocol.link;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import link.luyu.protocol.algorithm.MockSignatureAlgorithm;
import link.luyu.protocol.blockchain.MockBlockchain;
import link.luyu.protocol.common.STATUS;
import link.luyu.protocol.network.Account;
import link.luyu.protocol.network.Block;
import link.luyu.protocol.network.CallRequest;
import link.luyu.protocol.network.CallResponse;
import link.luyu.protocol.network.Events;
import link.luyu.protocol.network.Receipt;
import link.luyu.protocol.network.Resource;
import link.luyu.protocol.network.Transaction;
import link.luyu.protocol.utils.Utils;

public class HelloDriver implements Driver {

    private BlockChainCache blockChainCache = new BlockChainCache();
    private Map<String, Resource> resources = new HashMap<>();
    private Connection connection;
    private String chainPath;

    public HelloDriver(Connection connection, Map<String, Object> properties) {
        // Parse config info from properties
        this.chainPath = (String) properties.get("chainPath");

        // set connection
        this.connection = connection;

        // subscribe event
        subscribeNewBlockEvent(this.connection);
        subscribeNewResourceEvent(this.connection);
    }

    @Override
    public void start() throws RuntimeException {}

    @Override
    public void stop() throws RuntimeException {}

    @Override
    public void sendTransaction(Account account, Transaction request, ReceiptCallback callback) {
        // Encode transaction
        String txBytesStr = request.getMethod() + "(";
        for (String arg : request.getArgs()) {
            txBytesStr += arg;
        }
        txBytesStr += ")";
        byte[] txBytes = txBytesStr.getBytes(StandardCharsets.UTF_8);

        // Sign transaction
        account.sign(
                txBytes,
                new Account.SignCallback() {
                    @Override
                    public void onResponse(int status, String message, byte[] signBytes) {
                        if (status != STATUS.OK) {
                            System.out.println("Account sign error(" + status + "): " + message);
                        } else {
                            // Send
                            connection.asyncSend(
                                    request.getPath(),
                                    HelloConnection.SEND_TRANSACTION,
                                    signBytes,
                                    new Connection.Callback() {
                                        @Override
                                        public void onResponse(
                                                int errorCode,
                                                String message,
                                                byte[] responseData) {
                                            // assume this demo always response ok

                                            // waiting blockHeader syncing
                                            long needBlockNumber =
                                                    parseBlockNumberFromReceiptAndProof(
                                                            responseData);
                                            while (getBlockNumber() != needBlockNumber) {
                                                try {
                                                    System.out.println(
                                                            "Waiting for block-" + needBlockNumber);
                                                    Thread.sleep(1000); // You can use Future<> to
                                                    // optimize here
                                                } catch (Exception e) {
                                                }
                                            }
                                            byte[] blockHeaderBytes =
                                                    blockChainCache.getHeaderBytes(needBlockNumber);

                                            // verify transaction on-chain proof
                                            if (verifyTransactionAndOnChainProof(
                                                    blockHeaderBytes, responseData)) {
                                                // response receipt
                                                Receipt receipt = new Receipt();
                                                receipt.setResult(
                                                        new String[] {new String(responseData)});
                                                receipt.setBlockNumber(needBlockNumber);
                                                receipt.setMethod(request.getMethod());
                                                receipt.setArgs(request.getArgs());
                                                receipt.setPath(request.getPath());
                                                receipt.setCode(0); // SUCCESS
                                                receipt.setMessage("Success");
                                                receipt.setTransactionBytes(responseData);
                                                receipt.setTransactionHash(
                                                        new String(responseData));
                                                callback.onResponse(STATUS.OK, "Success", receipt);
                                            } else {
                                                callback.onResponse(
                                                        STATUS.INTERNAL_ERROR,
                                                        "Proof verify failed of tx: "
                                                                + new String(responseData),
                                                        null);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    @Override
    public void call(Account account, CallRequest request, CallResponseCallback callback) {
        // Encode call payload
        String callPayloadStr = request.getMethod() + "(";
        for (String arg : request.getArgs()) {
            callPayloadStr += arg;
        }
        callPayloadStr += ")";
        byte[] callPayload = callPayloadStr.getBytes(StandardCharsets.UTF_8);

        // Sign payload if your blockchain needed
        account.sign(
                callPayload,
                new Account.SignCallback() {
                    @Override
                    public void onResponse(int status, String message, byte[] signBytes) {
                        if (status != STATUS.OK) {
                            System.out.println("Account sign error(" + status + "): " + message);
                            callback.onResponse(status, message, null);
                        } else {
                            connection.asyncSend(
                                    request.getPath(),
                                    HelloConnection.SEND_TRANSACTION,
                                    signBytes,
                                    new Connection.Callback() {
                                        @Override
                                        public void onResponse(
                                                int errorCode,
                                                String message,
                                                byte[] responseData) {
                                            // assume this demo always response ok
                                            CallResponse callResponse = new CallResponse();
                                            callResponse.setResult(
                                                    new String[] {new String(responseData)});
                                            callResponse.setCode(0); // original receipt status
                                            callResponse.setMessage("Success");
                                            callResponse.setMethod(request.getMethod());
                                            callResponse.setArgs(request.getArgs());
                                            callResponse.setPath(request.getPath());
                                            callback.onResponse(STATUS.OK, "Success", callResponse);
                                        }
                                    });
                        }
                    }
                });
    }

    @Override
    public void getTransactionReceipt(String txHash, ReceiptCallback callback) {
        connection.asyncSend(
                null,
                HelloConnection.GET_TRANSACTION_RECEIPT,
                txHash.getBytes(StandardCharsets.UTF_8),
                new Connection.Callback() {
                    @Override
                    public void onResponse(int errorCode, String message, byte[] responseData) {
                        // assume this demo always response ok

                        // waiting blockHeader syncing
                        long needBlockNumber = parseBlockNumberFromReceiptAndProof(responseData);
                        while (getBlockNumber() < needBlockNumber) {
                            try {
                                System.out.println("Waiting for block-" + needBlockNumber);
                                Thread.sleep(1000); // You can use Future<> to optimize here
                            } catch (Exception e) {
                            }
                        }
                        byte[] blockHeaderBytes = blockChainCache.getHeaderBytes(needBlockNumber);

                        // verify transaction on-chain proof
                        if (verifyTransactionAndOnChainProof(blockHeaderBytes, responseData)) {
                            // response receipt
                            Receipt receipt = new Receipt();
                            receipt.setResult(new String[] {responseData.toString()});
                            receipt.setBlockNumber(needBlockNumber);
                            // receipt.setMethod(request.getMethod());
                            // receipt.setArgs(request.getArgs());
                            // receipt.setPath(request.getPath());
                            receipt.setCode(0); // SUCCESS
                            receipt.setMessage("Success");
                            receipt.setTransactionBytes(responseData);
                            receipt.setTransactionHash(new String(responseData));
                            callback.onResponse(STATUS.OK, "Success", receipt);
                        } else {
                            callback.onResponse(
                                    STATUS.INTERNAL_ERROR,
                                    "Proof verify failed of tx: " + new String(responseData),
                                    null);
                        }
                    }
                });
    }

    @Override
    public void getBlockByHash(String blockHash, BlockCallback callback) {
        long blockNumber = Long.parseLong(blockHash);
        getBlockByNumber(blockNumber, callback);
    }

    @Override
    public void getBlockByNumber(long blockNumber, BlockCallback callback) {
        Block block = blockChainCache.getBlock(blockNumber);
        if (block == null) {
            byte[] numberBytes = Utils.longToBytes(blockNumber);
            connection.asyncSend(
                    chainPath,
                    HelloConnection.GET_BLOCK_BY_NUMBER,
                    numberBytes,
                    new Connection.Callback() {
                        @Override
                        public void onResponse(int errorCode, String message, byte[] responseData) {
                            // responseData is block bytes
                            long blockNumber = MockBlockchain.Block.parseBlockNumber(responseData);

                            // Add block in blockchain cache
                            Block block = new Block();
                            block.setNumber(blockNumber);
                            block.setBytes(responseData);
                            block.setParentHash(new String[] {"aabbccdd"});
                            blockChainCache.setBlock(block);

                            callback.onResponse(errorCode, message, block);
                        }
                    });
        } else {
            callback.onResponse(STATUS.OK, "Success", blockChainCache.getBlock(blockNumber));
        }
    }

    @Override
    public long getBlockNumber() {
        return blockChainCache.getBlockNumber();
    }

    @Override
    public String getType() {
        return "Hello1.0";
    }

    @Override
    public String getSignatureType() {
        return MockSignatureAlgorithm.TYPE;
    }

    @Override
    public void listResources(ResourcesCallback callback) {
        callback.onResponse(STATUS.OK, "Success", resources.values().toArray(new Resource[0]));
    }

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
                        block.setParentHash(new String[] {"aabbccdd"});

                        // verify continuity of receive chain
                        String lastBlockHash = "aabbccdd";
                        if (block.getParentHash()[0].equals(lastBlockHash)) {
                            blockChainCache.setBlock(block);
                            System.out.println(
                                    "Update blockchain cache, blockNumber: " + blockNumber);
                        } else {
                            System.out.println("Recieve illegal block");
                        }
                    }
                });
    }

    private void subscribeNewResourceEvent(Connection connection) {
        connection.subscribe(
                HelloConnection.EVENT_RESOURCES_CHANGED,
                null,
                new Connection.Callback() {
                    @Override
                    public void onResponse(int errorCode, String message, byte[] responseData) {
                        String encodedStr = new String(responseData);
                        String[] resourceNames = encodedStr.split(",");
                        for (String resourceName : resourceNames) {
                            if (resourceName == null || resourceName.isEmpty()) {
                                continue;
                            }

                            if (!resources.containsKey(resourceName)) {
                                System.out.println("Detect new resource: " + resourceName);
                            }

                            Resource resource = new Resource();
                            resource.setPath(chainPath + "." + resourceName);
                            resource.setType(getType());
                            resource.setMethods(null);
                            resource.setProperties(null);

                            resources.put(resourceName, resource);
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
