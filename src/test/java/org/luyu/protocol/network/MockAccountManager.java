package org.luyu.protocol.network;

import org.luyu.protocol.algorithm.*;
import org.luyu.protocol.algorithm.ecdsa.secp256k1.EcdsaSecp256k1WithSHA256;
import org.luyu.protocol.algorithm.sm2.SM2WithSM3;

public class MockAccountManager implements AccountManager {

    @Override
    public Account getAccountBySignature(String signatureType, byte[] LuyuSign, LuyuSignData data) {
        return getAccountByIdentity(signatureType, data.getSender());
    }

    @Override
    public Account getAccountByIdentity(String signatureType, String identity) {
        switch (signatureType) {
            case MockSignatureAlgorithm.TYPE:
                return new MockAccount();
            case SM2WithSM3.TYPE:
            case EcdsaSecp256k1WithSHA256.TYPE:
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
