package org.luyu.protocol.algorithm;

import java.util.LinkedList;
import java.util.List;
import org.luyu.protocol.algorithm.ecdsa.secp256k1.EcdsaSecp256k1WithSHA256;
import org.luyu.protocol.algorithm.ecdsa.secp256r1.EcdsaSecp256r1;
import org.luyu.protocol.algorithm.sm2.SM2WithSM3;

public class SignAlgManager {
    public static List<String> getAllAlgTypes() {
        List<String> types = new LinkedList<>();
        types.add(SM2WithSM3.TYPE);
        types.add(EcdsaSecp256k1WithSHA256.TYPE);
        types.add(EcdsaSecp256r1.TYPE);
        return types;
    }

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
