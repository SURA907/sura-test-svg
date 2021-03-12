package org.luyu.protocol.link;

public interface Connection {
    /** Callback of asyncSend() function */
    interface Callback {
        /**
         * On response
         *
         * @param errorCode The errorCode according with diffrent implementation
         * @param message The description of errorCode
         * @param responseData Response binary package data, should decode to use it
         */
        void onResponse(int errorCode, String message, byte[] responseData);
    }

    /**
     * Send binary package data to certain block chain connection. Define type in your
     * implementation to separate different kinds of data
     *
     * @param path The luyu path to original blockchain
     * @param type The type defined by implementation to separate different kinds of data
     * @param data The binary package data, encode according with different implementation
     * @param callback
     */
    void asyncSend(String path, int type, byte[] data, Callback callback);

    /**
     * Subscribe callback by sending binary package data to certain block chain connection. Define
     * type in your implementation to separate different kinds of data
     *
     * @param type The type defined by implementation to separate different kinds of data
     * @param data The binary package data, encode according with different implementation
     * @param callback
     */
    void subscribe(int type, byte[] data, Callback callback);
}
