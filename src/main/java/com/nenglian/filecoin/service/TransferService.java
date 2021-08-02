package com.nenglian.filecoin.service;

import com.alibaba.fastjson.JSON;
import com.nenglian.filecoin.rpc.domain.cid.Cid;
import com.nenglian.filecoin.rpc.domain.exitcode.ExitCode;
import com.nenglian.filecoin.rpc.domain.types.Message;
import com.nenglian.filecoin.rpc.domain.types.SignedMessage;
import com.nenglian.filecoin.service.api.EstimatedGas;
import com.nenglian.filecoin.service.api.MQTxMessage;
import com.nenglian.filecoin.service.api.Transfer;
import com.nenglian.filecoin.service.db.Order;
import com.nenglian.filecoin.service.db.OrderRepository;
import com.nenglian.filecoin.transaction.TransactionListener;
import com.nenglian.filecoin.transaction.TransactionManager;
import com.nenglian.filecoin.transaction.TransactionSerializer;
import com.nenglian.filecoin.transaction.dto.TxReceipt;
import com.nenglian.filecoin.wallet.Wallet;
import java.math.BigInteger;
import java.util.Date;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException.NotImplemented;

/**
 * @author stephen
 * @since 2021/7/28 下午12:15
 */

@Service
public class TransferService {

    private static final Logger logger = LoggerFactory.getLogger(TransferService.class);

    @Autowired
    Wallet wallet;
    @Autowired
    OrderRepository repository;
    @Autowired
    TransactionListener listener;

    @Autowired
    TransactionManager txm;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;


    public EstimatedGas estimateGas(Transfer transfer){

        Message gasMessage = txm.estimateGas(transfer);
        BigInteger totalFee = getTotalFee(gasMessage);
        return EstimatedGas.builder()
            .gas(totalFee)
            .data(JSON.toJSONString(gasMessage))
            .build();
    }

    public String transfer(Transfer transfer) {
        logger.info("receive transfer request:{}", transfer);
        if (transfer.getTokenAddress() != null){
            throw new RuntimeException("Not implemented");
        }
        // check if we have had this one
        Order orderById = repository.findOrderByReqId(transfer.getReqId());
        if (orderById != null) {
            throw new RuntimeException("duplicated order");
        }

        String gasData = transfer.getGasData();

        Message gasMessage;
        if (gasData == null) {
            gasMessage = txm.estimateGas(transfer);
        }else {
             gasMessage = JSON.parseObject(gasData, Message.class);
        }

        BigInteger totalFee = getTotalFee(gasMessage);
        BigInteger balance = wallet.balance(transfer.getFrom());
        BigInteger value = transfer.getValue();

        if (balance.compareTo(totalFee.add(value)) < 0){
            throw new RuntimeException(String.format("balance: %s not enough, fee: %s, vale: %s", balance, totalFee, value));
        }

        SignedMessage signedMessage = txm.sign(transfer, gasMessage, null);

        // 首先落库, so we can handle all the exception cases
        Order order = Order.builder()
            .reqId(transfer.getReqId())
            .transfer(transfer)
            .status(TransferStatus.PENDING)
            .txId(new TransactionSerializer().getCid(signedMessage).toString())
            .build();
        repository.save(order);

        Cid cid = txm.send(signedMessage);
        return order.getTxId();
    }

    private BigInteger getTotalFee(Message gasMessage) {
        return BigInteger.valueOf(gasMessage.getGasLimit()).multiply(gasMessage.getGasFeeCap());
    }


    @EventListener
    public void handleTxEvent(TxReceipt txReceipt) {
        if (!wallet.isOurs(txReceipt))
            return;
        Order order = repository.findOrderByTxId(txReceipt.getCid().getStr());

        if (order != null) {
            handleOrderResult(txReceipt, order);
        }else {
            // 充值等交易
            handleTransferResult(txReceipt);
        }
    }

    private void handleTransferResult(TxReceipt txReceipt){
        MQTxMessage mq = MQTxMessage.builder()
            .reqId(null)
            .txId(txReceipt.getCid().getStr())
            .chainName("filecoin")
            .tokenAddress("")
            .from(txReceipt.getMessage().getFrom())
            .to(txReceipt.getMessage().getTo())
            .value(txReceipt.getMessage().getValue())
            .fee(txReceipt.getInvocResult().getGasCost().getTotalCost())
            .blockHeight(txReceipt.getBlockHeight())
            .blockTime(new Date(txReceipt.getBlockTime()))
            .build();

        if (txReceipt.getReceipt().getExitCode().equals(ExitCode.Ok)) {
            mq.setStatus((byte)2);
        } else {
            mq.setStatus((byte)3);
        }
        rocketMQTemplate.convertAndSend("filecoin", mq);
    }

    private void handleOrderResult(TxReceipt txReceipt, Order order) {
        if (!order.getStatus().equals(TransferStatus.PENDING)){
            logger.warn("duplicated tx, cid:{}", txReceipt.getMessage().getCid());
            return;
        }

        logger.info("收到transfer交易结果, cid:{}", txReceipt.getMessage().getCid());
        MQTxMessage mq = MQTxMessage.builder()
            .reqId(order.getReqId())
            .txId(txReceipt.getCid().getStr())
            .chainName("filecoin")
            .tokenAddress("")
            .from(txReceipt.getMessage().getFrom())
            .to(txReceipt.getMessage().getTo())
            .value(txReceipt.getMessage().getValue())
            .fee(txReceipt.getInvocResult().getGasCost().getTotalCost())
            .blockHeight(txReceipt.getBlockHeight())
            .blockTime(new Date(txReceipt.getBlockTime()))
            .build();

        if (txReceipt.getReceipt().getExitCode().equals(ExitCode.Ok)) {
            order.setStatus(TransferStatus.OK);
            mq.setStatus((byte)2);
        } else {
            order.setStatus(TransferStatus.FAIL);
            mq.setStatus((byte)3);
        }
        repository.save(order);
        rocketMQTemplate.convertAndSend("filecoin", mq);
    }


}
