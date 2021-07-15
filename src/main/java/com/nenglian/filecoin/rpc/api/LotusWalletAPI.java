package com.nenglian.filecoin.rpc.api;

import com.nenglian.filecoin.rpc.domain.types.KeyInfo;
import com.nenglian.filecoin.rpc.domain.types.Message;
import com.nenglian.filecoin.rpc.domain.types.SignedMessage;
import com.nenglian.filecoin.rpc.jasonrpc.Call;
import com.nenglian.filecoin.rpc.jasonrpc.JsonRpcParamsMode;
import com.nenglian.filecoin.rpc.jasonrpc.annotation.JsonRpcMethod;
import com.nenglian.filecoin.rpc.jasonrpc.annotation.JsonRpcService;
import java.math.BigInteger;
import java.util.List;

/**
 * @author stephen
 */
@JsonRpcService
public interface LotusWalletAPI {

    @JsonRpcMethod(value = "Filecoin.WalletBalance", paramsPassMode = JsonRpcParamsMode.ARRAY)
    Call<BigInteger> balance(String address);

    @JsonRpcMethod(value = "Filecoin.WalletDefaultAddress")
    Call<String> defaultAddress();

    @JsonRpcMethod(value = "Filecoin.WalletList")
    Call<List<String>> list();

    @JsonRpcMethod(value = "Filecoin.WalletNew", paramsPassMode = JsonRpcParamsMode.ARRAY)
    Call<String> create(String type);

    @JsonRpcMethod(value = "Filecoin.WalletExport", paramsPassMode = JsonRpcParamsMode.ARRAY)
    Call<KeyInfo> export(String address);

    @JsonRpcMethod(value = "Filecoin.WalletImport", paramsPassMode = JsonRpcParamsMode.ARRAY)
    Call<String> importKey(KeyInfo keyInfo);

    @JsonRpcMethod(value = "Filecoin.WalletSignMessage")
    Call<SignedMessage> signMessage(String address, Message message);

    @JsonRpcMethod(value = "Filecoin.WalletValidateAddress")
    Call<String> validateAddress(String address);


}
