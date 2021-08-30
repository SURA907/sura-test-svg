package link.luyu.protocol.network;

public interface LuyuSign {
    byte[] sign(byte[] secKey, LuyuSignData data);

    boolean verify(byte[] signBytes, LuyuSignData data);

    String recover(byte[] signBytes, LuyuSignData data); // recover identity from signBytes

    String secKey2Identity(
            byte[] secKey); // Identity means an unique name generated from key. eg: address

    String pubKey2Identity(
            byte[] pubKey); // Identity means an unique name generated from key. eg: address
}
