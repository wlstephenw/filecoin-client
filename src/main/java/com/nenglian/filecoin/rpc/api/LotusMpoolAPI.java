package com.nenglian.filecoin.rpc.api;

import com.nenglian.filecoin.rpc.domain.Message;
import com.nenglian.filecoin.rpc.domain.MessageSendSpec;
import com.nenglian.filecoin.rpc.domain.cid.Cid;
import com.nenglian.filecoin.rpc.domain.types.MpoolConfig;
import com.nenglian.filecoin.rpc.domain.types.SignedMessage;
import com.nenglian.filecoin.rpc.domain.types.TipSetKey;
import com.nenglian.filecoin.rpc.jasonrpc.*;
import com.nenglian.filecoin.rpc.jasonrpc.annotation.*;
import java.util.List;

/**
 * @author stephen
 */
@JsonRpcService
public interface LotusMpoolAPI {

    @JsonRpcMethod(value = "Filecoin.MpoolGetNonce", paramsPassMode = JsonRpcParamsMode.ARRAY)
    Call<Long> getNonce(String address);

    @JsonRpcMethod(value = "Filecoin.MpoolPending", paramsPassMode = JsonRpcParamsMode.ARRAY)
    Call<List<SignedMessage>> pending(TipSetKey tsk);

    @JsonRpcMethod(value = "Filecoin.MpoolSelect")
    Call<List<SignedMessage>> select(TipSetKey tsk, double tq);

    @JsonRpcMethod(value = "Filecoin.MpoolPush", paramsPassMode = JsonRpcParamsMode.ARRAY)
    Call<Cid> push(SignedMessage message);

    @JsonRpcMethod(value = "Filecoin.MpoolPushUntrusted", paramsPassMode = JsonRpcParamsMode.ARRAY)
    Call<Cid> pushUntrusted(SignedMessage message);

    @JsonRpcMethod(value = "Filecoin.MpoolPushMessage")
    Call<SignedMessage> pushMessage(Message message, MessageSendSpec spec);

    @JsonRpcMethod(value = "Filecoin.MpoolBatchPush")
    Call<List<Cid>> batchPush(List<SignedMessage> messages);

    @JsonRpcMethod(value = "Filecoin.MpoolBatchPushUntrusted")
    Call<List<Cid>> batchPushUntrusted(List<SignedMessage> messages);

    @JsonRpcMethod(value = "Filecoin.MpoolBatchPushMessage")
    Call<List<SignedMessage>> batchPushMessage(List<Message> messages, MessageSendSpec spec);

    @JsonRpcMethod(value = "Filecoin.MpoolClear", paramsPassMode = JsonRpcParamsMode.ARRAY)
    Call<Void> clear(Boolean flag);

    @JsonRpcMethod(value = "Filecoin.MpoolGetConfig")
    Call<MpoolConfig> getConfig();

    @JsonRpcMethod(value = "Filecoin.MpoolSetConfig")
    Call<Void> setConfig(MpoolConfig config);
}
