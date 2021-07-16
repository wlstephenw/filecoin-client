package com.nenglian.filecoin.transaction;

import cn.hutool.core.util.StrUtil;
import com.nenglian.filecoin.rpc.api.LotusAPIFactory;
import com.nenglian.filecoin.rpc.api.LotusGasAPI;
import com.nenglian.filecoin.rpc.domain.MessageSendSpec;
import com.nenglian.filecoin.rpc.domain.cid.Cid;
import com.nenglian.filecoin.rpc.domain.types.Message;
import com.nenglian.filecoin.rpc.domain.types.SignedMessage;
import com.nenglian.filecoin.rpc.domain.types.TipSetKey;
import com.nenglian.filecoin.rpc.jasonrpc.Response;
import com.nenglian.filecoin.wallet.Address;
import com.nenglian.filecoin.wallet.Wallet;
import com.nenglian.filecoin.service.api.EasyTransfer;
import java.io.IOException;
import java.math.BigInteger;
import org.springframework.stereotype.Component;

/**
 * @author stephen
 * @since 2021/7/13 下午5:43
 */

// TODO all runtime exception needs to be checked and see if they can be checked exception
@Component
public class TransactionManager {

    Wallet wallet;

    public TransactionManager(Wallet wallet) {
        this.wallet = wallet;
    }

    private static final String API_ROUTER = "http://localhost:7777/rpc/v1";
    private static final String AUTHORIZATION = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJBbGxvdyI6WyJyZWFkIl19.fNgcqyigMfozXVmBK13lhzPqDrjE3TwRDvcrwx9ReM0";
    protected LotusAPIFactory lotusAPIFactory = new LotusAPIFactory.Builder()
        .apiGateway(API_ROUTER)
        .authorization(AUTHORIZATION)
        .connectTimeout(5)
        .readTimeout(60)
        .writeTimeout(30)
        .build();

    private final LotusGasAPI lotusGasAPI = lotusAPIFactory.createLotusGasAPI();



    public Address createAddress(){

        return null;
    }

    public Message getGas(Message message){

        MessageSendSpec messageSendSpec = null;
        TipSetKey tsk = null;
        try {
            Response<Message> response = lotusGasAPI.estimateMessageGas(message, messageSendSpec, tsk).execute();
            Message result = response.getResult();
            return Message.builder()
                .gasFeeCap(result.getGasFeeCap())
                .gasLimit(result.getGasLimit())
                .gasPremium(result.getGasPremium()).build();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public long getNonce(String address){
        Response<Long> response = null;
        try {
            response = lotusAPIFactory.createLotusMPoolAPI().getNonce(address).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response.getResult();
    }

    public Cid easyTransfer(EasyTransfer send){
        if (send == null || StrUtil.isBlank(send.getFrom())
            || StrUtil.isBlank(send.getTo())
            || send.getValue() == null) {
            throw new RuntimeException("parameter cannot be empty");
        }
        //获取gas
        Message gas = getGas(Message.builder().from(send.getFrom())
            .to(send.getTo())
            .value(send.getValue()).build());
        //获取nonce
        long nonce = getNonce(send.getFrom());
        //拼装交易参数
        Message transaction = Message.builder().from(send.getFrom())
            .to(send.getTo())
            .gasFeeCap(gas.getGasFeeCap())
            .gasLimit(gas.getGasLimit() * 2)
            .gasPremium(gas.getGasPremium())
            .method(0L)
            .nonce( nonce)
            .params("")
            .value(new BigInteger(send.getValue().toString())).build();

         return send(transaction);
    }

    public Cid send(Message transaction){
        if (transaction == null || StrUtil.isBlank(transaction.getFrom())
            || StrUtil.isBlank(transaction.getTo())
            || transaction.getGasLimit() == null
            || transaction.getMethod() == null
            || transaction.getNonce() == null)
        {
            throw new RuntimeException("parameter cnanot be empty");
        }

        BigInteger account = transaction.getValue();
        if (account.compareTo(BigInteger.ZERO) < 0) {
            throw new RuntimeException("the transfer amount must be greater than 0");
        }

        SignedMessage signedMessage = wallet.getSigner(transaction.getFrom()).sign(transaction);

        // TODO send to network
        try {
            return lotusAPIFactory.createLotusMPoolAPI().push(signedMessage).execute().getResult();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return signedMessage.getMessage().getCid();
    }









}
