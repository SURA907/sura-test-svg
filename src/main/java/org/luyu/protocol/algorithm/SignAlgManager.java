package org.luyu.protocol.algorithm;

public class SignAlgManager {
    public static SignatureAlgorithm getAlgorithm(String type) {
        switch (type) {
            case EcdsaSecp256k1.TYPE:
                return new EcdsaSecp256k1();
            case SM2.TYPE:
                return new SM2();
            default:
                return null;
        }
    }
}
