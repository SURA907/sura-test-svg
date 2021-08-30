package link.luyu.protocol.network;

import java.util.Arrays;
import java.util.Objects;

public class Block {
    private String chainPath; // Path of the blockchain. eg: payment.chain0
    private long number; // Block number
    private String hash; // Block Hash
    private String[] parentHash; // Block parent hash, support DAG
    private String[] roots; // Block roots array. eg: transaction root, state root or receipt root
    private long timestamp; // Block timestamp
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

    public String[] getParentHash() {
        return parentHash;
    }

    public void setParentHash(String[] parentHash) {
        this.parentHash = parentHash;
    }

    public String[] getRoots() {
        return roots;
    }

    public void setRoots(String[] roots) {
        this.roots = roots;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    @Override
    public String toString() {
        return "Block{"
                + "chainPath='"
                + chainPath
                + '\''
                + ", number="
                + number
                + ", hash='"
                + hash
                + '\''
                + ", parentHash='"
                + parentHash
                + '\''
                + ", roots="
                + Arrays.toString(roots)
                + ", bytes="
                + Arrays.toString(bytes)
                + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Block)) return false;
        Block block = (Block) o;
        return getNumber() == block.getNumber()
                && getTimestamp() == block.getTimestamp()
                && Objects.equals(getChainPath(), block.getChainPath())
                && Objects.equals(getHash(), block.getHash())
                && Arrays.equals(getParentHash(), block.getParentHash())
                && Arrays.equals(getRoots(), block.getRoots())
                && Arrays.equals(getBytes(), block.getBytes());
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(getChainPath(), getNumber(), getHash(), getTimestamp());
        result = 31 * result + Arrays.hashCode(getParentHash());
        result = 31 * result + Arrays.hashCode(getRoots());
        result = 31 * result + Arrays.hashCode(getBytes());
        return result;
    }
}
