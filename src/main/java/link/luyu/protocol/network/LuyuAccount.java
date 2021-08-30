package link.luyu.protocol.network;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

// Luyu Account
public class LuyuAccount {
    private LuyuSign signer;
    private String identity;
    private byte[] pubKey;
    private byte[] secKey;

    public LuyuAccount() {}

    public static LuyuAccount build(LuyuSign signer, byte[] pubKey) {
        LuyuAccount account = new LuyuAccount();
        String address = signer.pubKey2Identity(pubKey);
        account.setSigner(signer);
        account.setIdentity(address);
        account.setPubKey(pubKey);
        return account;
    }

    public static LuyuAccount build(LuyuSign signer, byte[] pubKey, byte[] secKey) {
        LuyuAccount account = new LuyuAccount();
        String address = signer.pubKey2Identity(pubKey);
        String addressFromSec = signer.secKey2Identity(secKey);
        if (!address.equals(addressFromSec)) {
            throw new RuntimeException(
                    "Build LuyuAccount pubKey and secKey are not the same! pubKey:"
                            + Arrays.toString(pubKey));
        }
        account.setSigner(signer);
        account.setIdentity(address);
        account.setPubKey(pubKey);
        account.setSecKey(secKey);
        return account;
    }

    private Map<String, Object> properties = new HashMap<>();

    public byte[] sign(LuyuSignData data) {
        if (secKey == null) {
            throw new RuntimeException(
                    "Account " + getIdentity() + " can not be signer for empty secret key");
        }

        return signer.sign(secKey, data);
    }

    public boolean verify(byte[] signBytes, LuyuSignData data) {
        if (!identity.equals(data.getSender())) {
            return false;
        } else {
            return signer.verify(signBytes, data);
        }
    }

    public void setSigner(LuyuSign signer) {
        this.signer = signer;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public void setPubKey(byte[] pubKey) {
        this.pubKey = pubKey;
    }

    public void setSecKey(byte[] secKey) {
        this.secKey = secKey;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public LuyuSign getSigner() {
        return signer;
    }

    public String getIdentity() {
        return identity;
    }

    public byte[] getPubKey() {
        return pubKey;
    }

    public byte[] getSecKey() {
        return secKey;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }
}
