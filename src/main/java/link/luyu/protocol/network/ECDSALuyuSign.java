package link.luyu.protocol.network;

import link.luyu.protocol.algorithm.ecdsa.secp256k1.EcdsaSecp256k1WithSHA256;

public class ECDSALuyuSign implements LuyuSign {
    private EcdsaSecp256k1WithSHA256 signer = new EcdsaSecp256k1WithSHA256();

    @Override
    public byte[] sign(byte[] secKey, LuyuSignData data) {
        try {
            byte[] message = data.toBytes();
            byte[] signBytes = signer.sign(secKey, message);
            return signBytes;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public boolean verify(byte[] signBytes, LuyuSignData data) {
        String recoveredSender = recover(signBytes, data);
        return data.getSender() == null ? false : data.getSender().equals(recoveredSender);
    }

    @Override
    public String recover(byte[] signBytes, LuyuSignData data) {
        try {
            byte[] message = data.toBytes();
            byte[] pubKey = EcdsaSecp256k1WithSHA256.recover(message, signBytes);
            String address = EcdsaSecp256k1WithSHA256.getAddress(pubKey);
            return address;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public String secKey2Identity(byte[] secKey) {
        byte[] pubKey = EcdsaSecp256k1WithSHA256.secKey2PubKey(secKey);
        return pubKey2Identity(pubKey);
    }

    @Override
    public String pubKey2Identity(byte[] pubKey) {
        return EcdsaSecp256k1WithSHA256.getAddress(pubKey);
    }
}
