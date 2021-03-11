package org.luyu.protocol.blockchain;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MockBlockchain {
    public static class Block {
        public long number;
        public ArrayList<Transaction> transactions = new ArrayList<>();

        public byte[] getBytes() {
            byte[] res = (number + "-block").getBytes();
            return res;
        }

        public byte[] getBlockHeaderBytes() {
            return "block-header".getBytes();
        }

        public static long parseBlockNumber(byte[] bytes) {
            String blockNumberStr = (new String(bytes)).split("-")[0];
            return Long.parseLong(blockNumberStr);
        }
    }

    class Transaction {
        public byte[] txBytes;
        public TxCallback callback;
    }

    public interface TxCallback {
        void onResponse(byte[] receipt, byte[] onChainProof);
    }

    public interface BlockEvent {
        void onNewBlock(byte[] blockBytes);
    }

    public MockBlockchain(String name) {
        this.name = name;
        start();
    }

    private String name;
    private ArrayList<Block> blocks = new ArrayList<>();
    private Set<Transaction> txPool = new HashSet<>();
    private Thread consensus;
    private Set<BlockEvent> blockEvent = new HashSet<>();
    ExecutorService executor = Executors.newFixedThreadPool(20);

    public void sendTransaction(String contractAddress, byte[] txBytes, TxCallback callback) {
        Transaction tx = new Transaction();
        tx.txBytes = txBytes;
        tx.callback = callback;
        synchronized (txPool) {
            txPool.add(tx);
        }
    }

    public byte[] call(String contractAddress, byte[] inputs) {
        return "output".getBytes(StandardCharsets.UTF_8);
    }

    public byte[] getTransactionReceipt(byte[] txHash) {
        return (getBlockNumber() + "-receipt").getBytes(StandardCharsets.UTF_8);
    }

    public long getBlockNumber() {
        return blocks.size() - 1;
    }

    public byte[] getBlockByNumber(long number) {
        if (blocks.size() >= number) {
            return null;
        }

        return blocks.get((int) number).getBytes();
    }

    public void registerBlockEvent(BlockEvent event) {
        blockEvent.add(event);
    }

    public void connect() {
        System.out.println("Blockchain connected: " + name);
    }

    private void start() {
        if (consensus == null) {
            consensus =
                    new Thread(
                            () -> {
                                while (true) {
                                    try {
                                        Thread.sleep(1000);
                                        consensusLoop();
                                    } catch (Exception e) {
                                        System.out.println("Exception: " + e);
                                    }
                                }
                            });
            consensus.start();
        }
    }

    private void consensusLoop() {
        // seal block
        Block block = new Block();
        synchronized (txPool) {
            txPool.forEach(
                    (Transaction tx) -> {
                        block.transactions.add(tx);
                    });
            txPool.clear();
        }

        // consensus meet
        blocks.add(block);
        long number = blocks.size() - 1;
        block.number = number;


        // tx callback
        block.transactions.forEach(
                (tx) -> {
                    executor.submit(() -> {
                        tx.callback.onResponse(
                                getTransactionReceipt(new byte[]{}), block.getBlockHeaderBytes());
                    });
                });

        // Simulate block sync time
        try {
            Thread.sleep((new SecureRandom()).nextInt(30) * 100);
        } catch (Exception e) {
        }

        // block event
        System.out.println("==> Block[" + number + "]: txSize:" + block.transactions.size());
        blockEvent.forEach(
                (event) -> {
                    event.onNewBlock(block.getBytes());
                });


    }
}
