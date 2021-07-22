package com.nenglian.filecoin.transaction;

import cn.hutool.core.codec.Base32;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import com.nenglian.filecoin.rpc.api.LotusAPIFactory;
import com.nenglian.filecoin.rpc.api.LotusGasAPI;
import com.nenglian.filecoin.rpc.domain.MessageSendSpec;
import com.nenglian.filecoin.rpc.domain.cid.Cid;
import com.nenglian.filecoin.rpc.domain.types.Message;
import com.nenglian.filecoin.rpc.domain.types.SignedMessage;
import com.nenglian.filecoin.rpc.domain.types.TipSetKey;
import com.nenglian.filecoin.rpc.jasonrpc.Response;
import com.nenglian.filecoin.service.api.Transfer;
import com.nenglian.filecoin.wallet.Wallet;
import java.io.IOException;
import java.math.BigInteger;
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

    public TransactionManager(Wallet wallet) {
        this.wallet = wallet;
    }

    protected LotusAPIFactory lotusAPIFactory = LotusAPIFactory.create();
    private final LotusGasAPI lotusGasAPI = lotusAPIFactory.createLotusGasAPI();

    private Message getGas(Message message){
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

    public Message estimateGas(Transfer tx){
        return getGas(Message.builder().from(tx.getFrom())
            .to(tx.getTo())
            .value(tx.getValue()).build());
    }

    public Cid signAndSend(Transfer tx){
        //获取gas
        Message gas = estimateGas(tx);
        return this.signAndSend(tx, gas);
    }

    public SignedMessage sign(Transfer tx){
        //获取gas
        Message gas = estimateGas(tx);
        return this.sign(tx, gas);
    }

    public Cid signAndSend(Transfer tx, Message gas){
        if (tx == null || StrUtil.isBlank(tx.getFrom())
            || StrUtil.isBlank(tx.getTo())
            || tx.getValue() == null) {
            throw new RuntimeException("parameter cannot be empty");
        }
        Message transaction = buildMessage(tx, gas);
        SignedMessage signedMessage = this.sign(transaction);
        return send(signedMessage);
    }

    public SignedMessage sign(Transfer tx, Message gas){
        if (tx == null || StrUtil.isBlank(tx.getFrom())
            || StrUtil.isBlank(tx.getTo())
            || tx.getValue() == null) {
            throw new RuntimeException("parameter cannot be empty");
        }
        Message transaction = buildMessage(tx, gas);
        return this.sign(transaction);
    }

    private Message buildMessage(Transfer tx, Message gas) {
        //获取nonce
        long nonce = getNonce(tx.getFrom());
        //拼装交易参数
        return Message.builder()
            .version(0L)
            .from(tx.getFrom())
            .to(tx.getTo())
            .gasFeeCap(gas.getGasFeeCap())
            .gasLimit(gas.getGasLimit() * 2)
            .gasPremium(gas.getGasPremium())
            .method(0L)
            .nonce(nonce)
            .params("")
            .value(new BigInteger(tx.getValue().toString())).build();
    }

    private SignedMessage sign(Message transaction){
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
        if (signedMessage.getMessage().getCid() == null) {
            signedMessage.getMessage().setCid(this.getTxId(transaction));
        }
        return signedMessage;
    }

    public Message getMessage(Cid cid){
        try {
            return lotusAPIFactory.createLotusChainAPI().getMessage(cid).execute().getResult();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Cid send(SignedMessage signedMessage){
        // TODO send to network
        try {
            Cid cid = lotusAPIFactory.createLotusMPoolAPI().push(signedMessage).execute().getResult();
            Message message = this.getMessage(cid);
            Cid msgCid = message.getCid();
            logger.info("sending tx, cid: {}, msgCid:{}, tx: {}", cid, msgCid, signedMessage);
            return msgCid;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return signedMessage.getMessage().getCid();
    }

    private Cid getTxId(Message transaction){
        byte[] cidHash = getcidHash(transaction);
        logger.info("cid Hash is: {}", HexUtil.encodeHexStr(cidHash));
        return Cid.of(Base32.encode(cidHash));
    }

    private byte[] getcidHash(Message transaction){
        TransactionSerializer transactionSerializer = new TransactionSerializer();
        byte[] cidHash = null;
        try {
            cidHash = transactionSerializer.transactionSerialize(transaction);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("transaction entity serialization failed");
        }
        return cidHash;
    }




}
