package org.luyu.protocol.network;

import org.luyu.protocol.algorithm.*;

public class MockAccountManager implements AccountManager {

    @Override
    public Account getAccountBySignature(String signatureType, byte[] LuyuSign) {
        return getAccountByIdentity(signatureType, new byte[] {});
    }

    @Override
    public Account getAccountByIdentity(String signatureType, byte[] identity) {
        switch (signatureType) {
            case MockSignatureAlgorithm.TYPE:
                return new MockAccount();
            case SM2.TYPE:
            case EcdsaSecp256k1.TYPE:
            default:
                return null; // unsupported
        }
    }

    /*
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
    */

}
