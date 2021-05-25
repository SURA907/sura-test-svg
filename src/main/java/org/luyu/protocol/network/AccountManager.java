package org.luyu.protocol.network;

public interface AccountManager {
    /**
     * Get account by luyu signature
     *
     * @param signatureType
     * @param LuyuSign
     * @return
     */
    Account getAccountBySignature(String signatureType, byte[] LuyuSign);

    /**
     * Get account by account's identity
     *
     * @param signatureType
     * @param identity
     * @return
     */
    Account getAccountByIdentity(String signatureType, byte[] identity);
}
