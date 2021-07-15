package com.nenglian.filecoin.rpc.jasonrpc;


import lombok.Getter;
import okhttp3.ResponseBody;

/**
 * @author stephen
 */
public final class Response<T> {

    @Getter
    private final okhttp3.Response rawResponse;

    @Getter
    private final T result;

    private Response(okhttp3.Response rawResponse, T result, ResponseBody errorBody) {
        this.rawResponse = rawResponse;
        this.result = result;
    }

    public static <T> Response<T> error(ResponseBody body, okhttp3.Response rawResponse) {
        Utils.checkNotNull(body, "body == null");
        Utils.checkNotNull(rawResponse, "rawResponse == null");

        if (rawResponse.isSuccessful()) {
            throw new IllegalArgumentException("rawResponse should not be successful response");
        }
        return new Response<>(rawResponse, null, body);
    }

    public static <T> Response<T> success(T result, okhttp3.Response rawResponse) {
        Utils.checkNotNull(rawResponse, "rawResponse == null");
        if (!rawResponse.isSuccessful()) {
            throw new IllegalArgumentException("rawResponse must be successful response");
        }
        return new Response<>(rawResponse, result, null);
    }
}
