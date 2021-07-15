package com.nenglian.filecoin.rpc.api;

import com.nenglian.filecoin.rpc.domain.BlockMessages;
import com.nenglian.filecoin.rpc.domain.types.Message;
import com.nenglian.filecoin.rpc.domain.cid.Cid;
import com.nenglian.filecoin.rpc.domain.types.BlockHeader;
import com.nenglian.filecoin.rpc.domain.types.MessageReceipt;
import com.nenglian.filecoin.rpc.domain.types.TipSet;
import com.nenglian.filecoin.rpc.domain.types.TipSetKey;
import com.nenglian.filecoin.rpc.jasonrpc.*;
import com.nenglian.filecoin.rpc.jasonrpc.annotation.*;
import java.math.BigInteger;
import java.util.List;

/**
 * @author stephen
 */
@JsonRpcService
public interface LotusChainAPI {

    @JsonRpcMethod("Filecoin.ChainHead")
    Call<TipSet> head();

    @JsonRpcMethod("Filecoin.ChainGetGenesis")
    Call<TipSet> getGenesis();

    @JsonRpcMethod(value = "Filecoin.ChainGetBlock", paramsPassMode = JsonRpcParamsMode.ARRAY)
    Call<BlockHeader> getBlock(Cid blockCid);

    @JsonRpcMethod(value = "Filecoin.ChainGetTipSet", paramsPassMode = JsonRpcParamsMode.ARRAY)
    Call<TipSet> getTipSet(TipSetKey tsk);

    @JsonRpcMethod(value = "Filecoin.ChainGetBlockMessages", paramsPassMode = JsonRpcParamsMode.ARRAY)
    Call<BlockMessages> getBlockMessages(Cid blockCid);

    @JsonRpcMethod(value = "Filecoin.ChainGetParentReceipts", paramsPassMode = JsonRpcParamsMode.ARRAY)
    Call<List<MessageReceipt>> getParentReceipts(Cid blockCid);

    @JsonRpcMethod(value = "Filecoin.ChainGetParentMessages", paramsPassMode = JsonRpcParamsMode.ARRAY)
    Call<List<com.nenglian.filecoin.rpc.domain.Message>> getParentMessages(Cid blockCid);

    @JsonRpcMethod(value = "Filecoin.ChainGetMessage", paramsPassMode = JsonRpcParamsMode.ARRAY)
    Call<Message> getMessage(Cid cid);

    @JsonRpcMethod(value = "Filecoin.ChainGetTipSetByHeight")
    Call<TipSet> getTipSetByHeight(Long chainEpoch, TipSetKey tsk);

    @JsonRpcMethod(value = "Filecoin.ChainHasObj", paramsPassMode = JsonRpcParamsMode.ARRAY)
    Call<Boolean> hasObj(Cid cid);

    @JsonRpcMethod(value = "Filecoin.ChainReadObj", paramsPassMode = JsonRpcParamsMode.ARRAY)
    Call<String> readObj(Cid cid);

    @JsonRpcMethod(value = "Filecoin.ChainDeleteObj", paramsPassMode = JsonRpcParamsMode.ARRAY)
    Call<Void> deleteObj(Cid cid);

    @JsonRpcMethod(value = "Filecoin.ChainStatObj")
    Call<Void> statObj(Cid objectCid, Cid baseCid);

    @JsonRpcMethod(value = "Filecoin.ChainTipSetWeight", paramsPassMode = JsonRpcParamsMode.ARRAY)
    Call<BigInteger> tipSetWeight(TipSetKey tsk);
}
