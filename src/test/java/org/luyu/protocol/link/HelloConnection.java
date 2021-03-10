package org.luyu.protocol.link;

public class HelloConnection implements Connection {
    @Override
    public void asyncSend(String path, long type, byte[] data, Callback callback) {}

    @Override
    public void subscribe(String path, long type, byte[] data, Callback callback) {}
}
