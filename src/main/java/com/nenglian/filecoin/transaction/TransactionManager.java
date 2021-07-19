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
import com.nenglian.filecoin.service.api.EasyTransfer;
import com.nenglian.filecoin.wallet.Address;
import com.nenglian.filecoin.wallet.Wallet;
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

    protected LotusAPIFactory lotusAPIFactory = LotusAPIFactory.create();
    private final LotusGasAPI lotusGasAPI = lotusAPIFactory.createLotusGasAPI();

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

    public Message estimateGas(EasyTransfer tx){
        return getGas(Message.builder().from(tx.getFrom())
            .to(tx.getTo())
            .value(tx.getValue()).build());
    }

    public Cid easyTransfer(EasyTransfer tx){
        //获取gas
        Message gas = estimateGas(tx);
        return this.easyTransfer(tx, gas);
    }

    public Cid easyTransfer(EasyTransfer tx, Message gas){
        if (tx == null || StrUtil.isBlank(tx.getFrom())
            || StrUtil.isBlank(tx.getTo())
            || tx.getValue() == null) {
            throw new RuntimeException("parameter cannot be empty");
        }
        //获取nonce
        long nonce = getNonce(tx.getFrom());
        //拼装交易参数
        Message transaction = Message.builder().from(tx.getFrom())
            .to(tx.getTo())
            .gasFeeCap(gas.getGasFeeCap())
            .gasLimit(gas.getGasLimit() * 2)
            .gasPremium(gas.getGasPremium())
            .method(0L)
            .nonce( nonce)
            .params("")
            .value(new BigInteger(tx.getValue().toString())).build();

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
