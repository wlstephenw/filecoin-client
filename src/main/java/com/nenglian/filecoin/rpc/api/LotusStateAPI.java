package com.nenglian.filecoin.rpc.api;

import com.nenglian.filecoin.rpc.domain.ActorState;
import com.nenglian.filecoin.rpc.domain.*;
import com.nenglian.filecoin.rpc.domain.types.Message;
import com.nenglian.filecoin.rpc.domain.MessageMatch;
import com.nenglian.filecoin.rpc.domain.MinerPower;
import com.nenglian.filecoin.rpc.domain.builtin.MinerInfo;
import com.nenglian.filecoin.rpc.domain.builtin.SectorOnChainInfo;
import com.nenglian.filecoin.rpc.domain.cid.Cid;
import com.nenglian.filecoin.rpc.domain.types.Actor;
import com.nenglian.filecoin.rpc.domain.types.MessageReceipt;
import com.nenglian.filecoin.rpc.domain.types.TipSetKey;
import com.nenglian.filecoin.rpc.jasonrpc.*;
import com.nenglian.filecoin.rpc.jasonrpc.annotation.*;
import java.util.List;

/**
 * @author stephen
 */
@JsonRpcService
public interface LotusStateAPI {

    // StateCall runs the given message and returns its result without any persisted changes.
    @JsonRpcMethod("Filecoin.StateCall")
    Call<InvocResult> call(Message message, TipSetKey tsk);

    // StateReplay replays a given message, assuming it was included in a block in the specified tipset.
    // If no tipset key is provided, the appropriate tipset is looked up.
    @JsonRpcMethod("Filecoin.StateReplay")
    Call<InvocResult> replay(TipSetKey tsk, Cid cid);

    // StateGetActor returns the indicated actor's nonce and balance.
    @JsonRpcMethod("Filecoin.StateGetActor")
    Call<Actor> getActor(String address, TipSetKey tsk);

    // StateReadState returns the indicated actor's state.
    @JsonRpcMethod("Filecoin.StateReadState")
    Call<ActorState> readState(String address, TipSetKey tsk);

    @JsonRpcMethod("Filecoin.StateListMessages")
    Call<List<Cid>> listMessages(MessageMatch messageMatch, TipSetKey tsk, Long chainEpoch);

    @JsonRpcMethod("Filecoin.StateMinerInfo")
    Call<MinerInfo> minerInfo(String address, TipSetKey tsk);

    @JsonRpcMethod("Filecoin.StateMinerPower")
    Call<MinerPower> minerPower(String address, TipSetKey tsk);

    @JsonRpcMethod("Filecoin.StateSectorGetInfo")
    Call<SectorOnChainInfo> sectorGetInfo(String address, Long sectorNumber, TipSetKey tsk);

    @JsonRpcMethod("Filecoin.StateMinerActiveSectors")
    Call<List<SectorOnChainInfo>> minerActiveSectors(String address, TipSetKey tsk);

    @JsonRpcMethod(value = "Filecoin.StateSearchMsg", paramsPassMode = JsonRpcParamsMode.ARRAY)
    Call<MsgLookup> searchMsg(Cid cid);

    @JsonRpcMethod(value = "Filecoin.StateGetReceipt", paramsPassMode = JsonRpcParamsMode.ARRAY)
    Call<MessageReceipt> getReceipt(Cid cid);
}
