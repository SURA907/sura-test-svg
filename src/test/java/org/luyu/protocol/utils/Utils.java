package org.luyu.protocol.utils;

public class Utils {
    public static byte[] bytesConcat(byte[] a, byte[] b) {
        byte[] res = new byte[a.length + b.length];
        System.arraycopy(a, 0, res, 0, a.length);
        System.arraycopy(b, 0, res, a.length, b.length);
        return res;
    }

    public static String getChainPath(String path) {
        // path format: aaa.bbb.ccc
        // return aaa.bbb
        String[] sp = path.split("\\.");
        return sp[0] + "." + sp[1];
    }

    public static String getResourceName(String path) {
        // path format: aaa.bbb.ccc
        // return ccc
        String[] sp = path.split("\\.");
        return sp[2];
    }
}
