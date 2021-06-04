package org.luyu.protocol.network;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

// Luyu Account
public class LuyuAccount {
    private LuyuSign signer;
    private String address;
    private byte[] pubKey;
    private byte[] secKey;

    LuyuAccount() {}

    public static LuyuAccount build(LuyuSign signer, byte[] pubKey) {
        LuyuAccount account = new LuyuAccount();
        String address = signer.pubKey2Address(pubKey);
        account.setSigner(signer);
        account.setAddress(address);
        account.setPubKey(pubKey);
        return account;
    }

    public static LuyuAccount build(LuyuSign signer, byte[] pubKey, byte[] secKey) {
        LuyuAccount account = new LuyuAccount();
        String address = signer.pubKey2Address(pubKey);
        String addressFromSec = signer.secKey2Address(secKey);
        if (!address.equals(addressFromSec)) {
            throw new RuntimeException(
                    "Build LuyuAccount pubKey and secKey are not the same! pubKey:"
                            + Arrays.toString(pubKey));
        }
        account.setSigner(signer);
        account.setAddress(address);
        account.setPubKey(pubKey);
        account.setSecKey(secKey);
        return account;
    }

    private Map<String, Object> properties = new HashMap<>();

    public byte[] sign(LuyuSignData data) {
        return signer.sign(secKey, data);
    }

    public boolean verify(byte[] signBytes, LuyuSignData data) {
        if (!address.equals(data.getSender())) {
            return false;
        } else {
            return signer.verify(signBytes, data);
        }
    }

    public void setSigner(LuyuSign signer) {
        this.signer = signer;
    }

    public void setAddress(String address) {
        this.address = address;
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
}
