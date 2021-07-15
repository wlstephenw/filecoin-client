package com.nenglian.filecoin.rpc.jasonrpc;

/**
 * @author stephen
 */
public interface Callback<T> {

    void onResponse(Call<T> call, Response<T> response);

    void onFailure(Call<T> call, Throwable t);
}
