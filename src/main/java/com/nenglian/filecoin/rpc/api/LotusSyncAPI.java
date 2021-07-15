package com.nenglian.filecoin.rpc.api;

import com.nenglian.filecoin.rpc.domain.SyncState;
import com.nenglian.filecoin.rpc.jasonrpc.Call;
import com.nenglian.filecoin.rpc.jasonrpc.annotation.JsonRpcMethod;
import com.nenglian.filecoin.rpc.jasonrpc.annotation.JsonRpcService;

/**
 * @author stephen
 */
@JsonRpcService
public interface LotusSyncAPI {

    @JsonRpcMethod("Filecoin.SyncState")
    Call<SyncState> state();
}
