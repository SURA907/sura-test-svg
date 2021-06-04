package org.luyu.protocol.common;

public class STATUS {
    public static final int OK = 0;
    public static final int INTERNAL_ERROR = 100; // driver internal error
    public static final int CONNECTION_EXCEPTION = 200; // query connection exception
    public static final int ROUTER_EXCEPTION = 300; // router exception
    public static final int ACCOUNT_MANAGER_EXCEPTION = 400;
}
