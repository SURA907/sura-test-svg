package org.luyu.protocol.network;

public class MockAccountManager implements AccountManager {
    @Override
    public ChainAccount verifyAndGetChainAccount(Transaction tx) {
        ChainAccount chainAccount = new ChainAccount();
        chainAccount.setSecKey(new byte[] {});
        return chainAccount;
    }

    @Override
    public ChainAccount getChainAccountByIdentity(
            byte[] identity, String fromChainType, String toChainType) {
        ChainAccount chainAccount = new ChainAccount();
        chainAccount.setSecKey(new byte[] {});
        return chainAccount;
    }
}
