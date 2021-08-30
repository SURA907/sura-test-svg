package link.luyu.protocol.network;

public interface AccountManager {
    /**
     * Get account by luyu signature
     *
     * @param signatureType
     * @param luyuSign
     * @return
     */
    Account getAccountBySignature(String signatureType, byte[] luyuSign, LuyuSignData data)
            throws Exception;

    /**
     * Get account by account's identity
     *
     * @param signatureType
     * @param identity
     * @return
     */
    Account getAccountByIdentity(String signatureType, String identity);
}
