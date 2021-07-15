package com.nenglian.filecoin.rpc.jasonrpc;

/**
 * @author stephen
 */
public class JsonConvertException extends RuntimeException {

    public JsonConvertException(String message) {
        super(message);
    }

    public JsonConvertException(String message, Throwable e) {
        super(message, e);
    }

    public JsonConvertException(Throwable e) {
        super(e);
    }
}
