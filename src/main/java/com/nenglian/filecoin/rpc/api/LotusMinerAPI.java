package com.nenglian.filecoin.rpc.api;

import com.nenglian.filecoin.rpc.domain.MiningBaseInfo;
import com.nenglian.filecoin.rpc.domain.types.TipSetKey;
import com.nenglian.filecoin.rpc.jasonrpc.Call;
import com.nenglian.filecoin.rpc.jasonrpc.annotation.JsonRpcMethod;
import com.nenglian.filecoin.rpc.jasonrpc.annotation.JsonRpcService;

/**
 * @author stephen
 */
@JsonRpcService
public interface LotusMinerAPI {

    @JsonRpcMethod("Filecoin.MinerGetBaseInfo")
    Call<MiningBaseInfo> getBaseInfo(String address, long chainEpoch, TipSetKey tsk);
}
