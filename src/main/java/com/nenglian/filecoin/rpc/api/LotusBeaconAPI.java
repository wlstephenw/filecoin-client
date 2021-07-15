package com.nenglian.filecoin.rpc.api;

import com.nenglian.filecoin.rpc.domain.types.BeaconEntry;
import com.nenglian.filecoin.rpc.jasonrpc.*;
import com.nenglian.filecoin.rpc.jasonrpc.annotation.*;


/**
 * @author stephen
 */
@JsonRpcService
public interface LotusBeaconAPI {

    @JsonRpcMethod(value = "Filecoin.BeaconGetEntry", paramsPassMode = JsonRpcParamsMode.ARRAY)
    Call<BeaconEntry> getEntry(Long chainEpoch);
}
