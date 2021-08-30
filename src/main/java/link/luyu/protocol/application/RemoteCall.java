package link.luyu.protocol.application;

public interface RemoteCall<T> {
    interface Callback<T> {
        /**
         * Callback of RemoteCall
         *
         * @param status STATUS
         * @param message error message
         * @param response
         */
        void onResponse(int status, String message, T response);
    }

    T send();

    void asyncSend(Callback<T> callback);
}
