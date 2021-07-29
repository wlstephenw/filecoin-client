package com.nenglian.filecoin.service;

import com.nenglian.filecoin.rpc.domain.exitcode.ExitCode;
import com.nenglian.filecoin.rpc.domain.types.Message;
import com.nenglian.filecoin.rpc.domain.types.SignedMessage;
import com.nenglian.filecoin.service.api.MQTxMessage;
import com.nenglian.filecoin.service.api.Result;
import com.nenglian.filecoin.service.api.Transfer;
import com.nenglian.filecoin.service.db.Account;
import com.nenglian.filecoin.service.db.Order;
import com.nenglian.filecoin.service.db.OrderRepository;
import com.nenglian.filecoin.transaction.TransactionListener;
import com.nenglian.filecoin.transaction.TransactionManager;
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

    public String transfer(Transfer transfer) {

        logger.info("receive transfer request:{}", transfer);
        String res = null;

        Order orderById = repository.findOrderById(transfer.getReqId());
        if (orderById != null) {
            throw new RuntimeException("duplicated order");
        }

        // 首先落库
        Order order = Order.builder().reqId(transfer.getReqId()).transfer(transfer).status(TransferStatus.PENDING).build();
        repository.save(order);

        Message gasMessage = txm.estimateGas(transfer);
        BigInteger totalFee = BigInteger.valueOf(gasMessage.getGasLimit()).multiply(gasMessage.getGasFeeCap());
        BigInteger balance = wallet.balance(transfer.getFrom());
        if (balance.compareTo(totalFee) >= 0){
            // money enough
            SignedMessage signedMessage = txm.sign(transfer, gasMessage, null);
            order.setStatus(TransferStatus.GAS_OK);
            order.setTxId(signedMessage.getMessage().getCid().getStr());
            repository.save(order);
            txm.send(signedMessage);

            MQTxMessage mq = MQTxMessage.builder()
                .reqId(order.getReqId())
                .build();
            mq.setTransferTxId(signedMessage.getMessage().getCid().getStr());
            rocketMQTemplate.convertAndSend("filecoin", mq);

        }else {
            Transfer gasTransfer = Transfer.builder()
                .from(transfer.getGasAddress())
                .to(transfer.getFrom())
                .value(gasMessage.getGasFeeCap().multiply(BigInteger.valueOf(gasMessage.getGasLimit())))
                .speedup(transfer.getSpeedup())
                .build();
            SignedMessage sign = txm.sign(gasTransfer, null, null);

            // 先落库
            order.setGasTxId(sign.getMessage().getCid().getStr());
            order.setGasMessage(gasMessage);
            order.setStatus(TransferStatus.PENDING);
            repository.save(order);
            txm.send(sign);
            res = sign.getMessage().getCid().getStr();
        }


        return res;
    }

    @EventListener
    public void handleTxEvent(TxReceipt txReceipt) {

        if (!wallet.isOurs(txReceipt))
            return;

        Order order = repository.findOrderByTxId(txReceipt.getMessage().getCid().getStr());
        Order gasOrder = repository.findOrderByGasTxId(txReceipt.getMessage().getCid().getStr());
        if (order != null) {
            if (!order.getStatus().equals(TransferStatus.GAS_OK)){
                logger.info("duplicated tx, cid:{}", txReceipt.getMessage().getCid());
                return;
            }

            logger.info("收到transfer交易结果, cid:{}", txReceipt.getMessage().getCid());
            MQTxMessage mq = MQTxMessage.builder()
                .reqId(order.getReqId())
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
        } else if (gasOrder != null) {
            if (!gasOrder.getStatus().equals(TransferStatus.PENDING)){
                logger.info("duplicated tx, cid:{}", txReceipt.getMessage().getCid());
                return;
            }

            logger.info("收到gas转账交易结果, cid:{}", txReceipt.getMessage().getCid());
            MQTxMessage mq = MQTxMessage.builder()
                .reqId(gasOrder.getReqId())
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
                gasOrder.setStatus(TransferStatus.GAS_OK);
                mq.setStatus((byte)2);
            } else {
                gasOrder.setStatus(TransferStatus.GAS_FAIL);
                mq.setStatus((byte)3);
            }
            repository.save(gasOrder);
            if (gasOrder.getStatus().equals(TransferStatus.GAS_FAIL)) {
                rocketMQTemplate.convertAndSend("filecoin", mq);
                return;
            }

            SignedMessage signedMessage = txm.sign(gasOrder.getTransfer(), gasOrder.getGasMessage(), null);
            gasOrder.setTxId(signedMessage.getMessage().getCid().getStr());
            repository.save(gasOrder);

            txm.send(signedMessage);


            mq.setTransferTxId(signedMessage.getMessage().getCid().getStr());
            rocketMQTemplate.convertAndSend("filecoin", mq);
        }
    }

}
