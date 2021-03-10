package org.luyu.protocol.network;

public class Block {
    private String chainPath; // Path of the blockchain. eg: payment.chain0
    private long number; // Block number
    private String hash; // Block Hash
    private String parentHash; // Block parent hash
    private String[] roots; // Block roots array. eg: transaction root, state root or receipt root
    private byte[] bytes; // Original block bytes of a certain blockchain

    public String getChainPath() {
        return chainPath;
    }

    public void setChainPath(String chainPath) {
        this.chainPath = chainPath;
    }

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getParentHash() {
        return parentHash;
    }

    public void setParentHash(String parentHash) {
        this.parentHash = parentHash;
    }

    public String[] getRoots() {
        return roots;
    }

    public void setRoots(String[] roots) {
        this.roots = roots;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
}
