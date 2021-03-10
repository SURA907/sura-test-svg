package org.luyu.protocol.network;

public interface AccountManager {
    ChainAccount verifyAndGetChainAccount(Transaction tx);

    ChainAccount getChainAccountByIdentity(
            byte[] identity, String fromChainType, String toChainType);
}
