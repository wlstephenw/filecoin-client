package com.nenglian.filecoin.transaction;

import cn.hutool.core.codec.Base32;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import com.nenglian.filecoin.rpc.api.LotusAPIFactory;
import com.nenglian.filecoin.rpc.api.LotusGasAPI;
import com.nenglian.filecoin.rpc.domain.MessageSendSpec;
import com.nenglian.filecoin.rpc.domain.MsgLookup;
import com.nenglian.filecoin.rpc.domain.cid.Cid;
import com.nenglian.filecoin.rpc.domain.types.Message;
import com.nenglian.filecoin.rpc.domain.types.SignedMessage;
import com.nenglian.filecoin.rpc.domain.types.TipSetKey;
import com.nenglian.filecoin.rpc.jasonrpc.Response;
import com.nenglian.filecoin.service.api.Transfer;
import com.nenglian.filecoin.wallet.Wallet;
import com.nenglian.filecoin.wallet.signer.Signer;
import java.io.IOException;
import java.math.BigInteger;
import jnr.ffi.annotations.In;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author stephen
 * @since 2021/7/13 下午5:43
 */

// TODO all runtime exception needs to be checked and see if they can be checked exception
@Component
@Data
public class TransactionManager {
    private static final Logger logger = LoggerFactory.getLogger(TransactionManager.class);

    Wallet wallet;
    protected LotusAPIFactory lotusAPIFactory = LotusAPIFactory.create();
    private final LotusGasAPI lotusGasAPI = lotusAPIFactory.createLotusGasAPI();
    ReceiptProcessor receiptProcessor = new ReceiptProcessor(lotusAPIFactory, 1000, 1000);


    public TransactionManager(Wallet wallet) {
        this.wallet = wallet;
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



    public Message estimateGas(Transfer tx){

        Message gas = getGas(Message.builder().from(tx.getFrom())
            .to(tx.getTo())
            .value(tx.getValue()).build());
        if (tx.getSpeedup() != null) {
            return this.txSpeedup(gas, tx.getSpeedup());
        }else {
            return gas;
        }
    }


    public SignedMessage sign(Transfer tx, Message gas, Signer signer){
        if (tx == null || StrUtil.isBlank(tx.getFrom())
            || StrUtil.isBlank(tx.getTo())
            || tx.getValue() == null) {
            throw new RuntimeException("parameter cannot be empty");
        }
        if (gas == null)
            gas  = estimateGas(tx);
        Message transaction = buildMessage(tx, gas);
        if (signer == null)
            signer = this.wallet.getSigner(transaction.getFrom());
        return this.sign(transaction, signer);
    }

    Message buildMessage(Transfer tx, Message gas) {
        //获取nonce
        long nonce = getNonce(tx.getFrom());
        //拼装交易参数
        return Message.builder()
            .version(0L)
            .from(tx.getFrom())
            .to(tx.getTo())
            .gasFeeCap(gas.getGasFeeCap())
            .gasLimit(gas.getGasLimit())
            .gasPremium(gas.getGasPremium())
            .method(0L)
            .nonce(nonce)
            .params("")
            .value(new BigInteger(tx.getValue().toString())).build();
    }


    SignedMessage sign(Message transaction, Signer signer){
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
        return signer.sign(transaction);
    }

    public Cid send(SignedMessage signedMessage){
        // TODO send to network
        try {
            logger.info("sending tx, cid: {},  tx: {}", signedMessage.getMessage().getCid(), signedMessage);
            Cid cid = lotusAPIFactory.createLotusMPoolAPI().push(signedMessage).execute().getResult();
            return cid;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return signedMessage.getMessage().getCid();
    }

    public MsgLookup sendAndWait(SignedMessage signedMessage){
        Cid cid = this.send(signedMessage);
        return receiptProcessor.waitForTransactionReceipt(signedMessage.getMessage().getCid().getStr());
    }

    public Message txSpeedup(Message gasEstimation, Float speedup){
        BigInteger speedupPremium = gasEstimation.getGasPremium().multiply(new BigInteger(String.valueOf(speedup.intValue())));
        BigInteger speedupFeeCap = speedupPremium.add(gasEstimation.getGasFeeCap().subtract(gasEstimation.getGasPremium()));
        gasEstimation.setGasPremium(speedupPremium);
        gasEstimation.setGasFeeCap(speedupFeeCap);

        return gasEstimation;
    }

    private Message getGas(Message message){
        MessageSendSpec messageSendSpec = null;
        TipSetKey tsk = null;
        try {
            message.setGasLimit(500000L);
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

}
