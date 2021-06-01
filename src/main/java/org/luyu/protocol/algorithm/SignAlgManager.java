package org.luyu.protocol.algorithm;

import org.luyu.protocol.algorithm.ecdsa.secp256k1.EcdsaSecp256k1WithSHA256;
import org.luyu.protocol.algorithm.ecdsa.secp256r1.EcdsaSecp256r1;
import org.luyu.protocol.algorithm.sm2.SM2WithSM3;

public class SignAlgManager {
    public static SignatureAlgorithm getAlgorithm(String type) {
        switch (type) {
            case SM2WithSM3.TYPE:
                return new SM2WithSM3();
            case EcdsaSecp256k1WithSHA256.TYPE:
                return new EcdsaSecp256k1WithSHA256();
            case EcdsaSecp256r1.TYPE:
                return new EcdsaSecp256r1();
            default:
                return null;
        }
    }
}
