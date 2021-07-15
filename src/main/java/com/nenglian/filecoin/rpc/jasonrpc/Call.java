package com.nenglian.filecoin.rpc.jasonrpc;

import java.io.IOException;
import okhttp3.Request;

/**
 * @author stephen
 */
public interface Call<T> extends Cloneable {

    Response<T> execute() throws IOException;

    void enqueue(Callback<T> callback);

    boolean isExecuted();

    void cancel();

    boolean isCanceled();

    Call<T> clone();

    Request request();
}
