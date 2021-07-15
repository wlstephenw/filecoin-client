package com.nenglian.filecoin.rpc.api;

import com.nenglian.filecoin.rpc.domain.types.Message;
import com.nenglian.filecoin.rpc.domain.MessageSendSpec;
import com.nenglian.filecoin.rpc.domain.types.TipSetKey;
import com.nenglian.filecoin.rpc.jasonrpc.*;
import com.nenglian.filecoin.rpc.jasonrpc.annotation.*;
import java.math.BigInteger;

/**
 * @author stephen
 */
@JsonRpcService
public interface LotusGasAPI {

    @JsonRpcMethod("Filecoin.GasEstimateFeeCap")
    Call<BigInteger> estimateFeeCap(Message message, Long maxqueueblks, TipSetKey tsk);

    @JsonRpcMethod("Filecoin.GasEstimateGasLimit")
    Call<Long> estimateGasLimit(Message message, TipSetKey tsk);

    @JsonRpcMethod("Filecoin.GasEstimateGasPremium")
    Call<BigInteger> estimateGasPremium(Long nblocksincl, String sender, Long gaslimit, TipSetKey tsk);

    @JsonRpcMethod("Filecoin.GasEstimateMessageGas")
    Call<Message> estimateMessageGas(Message message, MessageSendSpec messageSendSpec, TipSetKey tsk);
}
