package com.nenglian.filecoin.service;

import cn.hutool.core.util.HexUtil;
import com.nenglian.filecoin.rpc.api.LotusAPIFactory;
import com.nenglian.filecoin.rpc.domain.cid.Cid;
import com.nenglian.filecoin.rpc.domain.exitcode.ExitCode;
import com.nenglian.filecoin.rpc.domain.types.Message;
import com.nenglian.filecoin.rpc.domain.types.SignedMessage;
import com.nenglian.filecoin.rpc.domain.types.TipSet;
import com.nenglian.filecoin.service.api.BlockInfo;
import com.nenglian.filecoin.service.api.MQTxMessage;
import com.nenglian.filecoin.service.api.Reconciliation;
import com.nenglian.filecoin.service.api.Result;
import com.nenglian.filecoin.service.api.Transfer;
import com.nenglian.filecoin.service.api.WalletAddress;
import com.nenglian.filecoin.service.db.Account;
import com.nenglian.filecoin.service.db.Order;
import com.nenglian.filecoin.service.db.OrderRepository;
import com.nenglian.filecoin.transaction.TransactionListener;
import com.nenglian.filecoin.transaction.TransactionManager;
import com.nenglian.filecoin.transaction.dto.TxEvent;
import com.nenglian.filecoin.wallet.Address;
import com.nenglian.filecoin.wallet.Convert;
import com.nenglian.filecoin.wallet.Wallet;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author stephen
 * @since 2021/7/13 下午5:04
 */
@RestController
public class Controller implements Api {
    private static final Logger logger = LoggerFactory.getLogger(Controller.class);

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


    @Override
    public Result<WalletAddress> createAddress(byte type) {
        WalletAddress address = wallet.createAddress();
        Result<WalletAddress> res = new Result<>();
        res.setCode(0);
        res.setData(address);
        return res;
    }

    @Override
    public Result<String> transfer(Transfer transfer) {

        logger.info("receive transfer request:{}", transfer);

        Order orderById = repository.findOrderById(transfer.getReqId());
        if (orderById != null) {
            return Result.<String>builder().data(orderById.getTxId()).msg("duplicated order").build();
        }

        // 首先落库
        Order order = Order.builder().reqId(transfer.getReqId()).transfer(transfer).status(TransferStatus.PENDING).build();
        repository.save(order);

        Message gasMessage = txm.estimateGas(transfer);
        Transfer gasTransfer = Transfer.builder()
            .from(transfer.getGasAddress())
            .to(transfer.getFrom())
            .value(gasMessage.getGasFeeCap()).build();
        Cid gasCid = txm.signAndSend(gasTransfer);
        order.setGasTxId(gasCid.getStr());
        order.setGasMessage(gasMessage);
        order.setStatus(TransferStatus.PENDING);
        repository.save(order);



        Result<String> res = new Result<>();
        res.setCode(0);
        res.setData(gasCid.getStr());
        return res;
    }


    @Override
    public Result<List<Reconciliation>> reconciliation(Date from, Date to) {
        try {
            CompletableFuture<Map<Cid, TxEvent>> future = listener
                .getMessagesFutureByHeightRange(from, to);
            Map<Cid, TxEvent> map = future.get();
            List<Reconciliation> collect = map.entrySet().stream()
                .map((e) -> Reconciliation.builder()
                    .txId(e.getKey().getStr())
                    .from(e.getValue().getMessage().getFrom())
                    .to(e.getValue().getMessage().getTo())
                    .value(e.getValue().getMessage().getValue())
                    .fee(e.getValue().getReceipt().getGasUsed())
                    .build())
                .collect(Collectors.toList());
            return Result.<List<Reconciliation>>builder().code(0).data(collect).build();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Result setPollHeight(Long fromHeight) {
        listener.getLatestBlock().getAndSet(fromHeight.intValue());
        return Result.builder().code(0).build();
    }

    @Override
    public Result<String> toAddress(String hexpk) {
        String address = new Address(HexUtil.decodeHex(hexpk)).toEncodedAddress();
        return Result.<String>builder().code(0).data(address).build();
    }


    @Override
    public Result<BlockInfo> latestBlock() {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        TipSet head = null;
        try {
            head = listener.head();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        BlockInfo blockInfo = BlockInfo.builder()
            .height(head.getHeight())
            .time(new Date(head.getBlocks().get(0).getTimestamp()))
            .build();
        return Result.<BlockInfo>builder().data(blockInfo).build();
    }

    @Override
    public Result<BigInteger> balance(String address, String tokenAddress) {
        BigInteger balance = wallet.balance(address);
        Result<BigInteger> res = new Result<>();
        res.setCode(0);
        res.setData(balance);
        return res;
    }


    @EventListener
    public void handleTxEvent(TxEvent txEvent) {

        Account from = wallet.get(txEvent.getMessage().getFrom());
        Account to = wallet.get(txEvent.getMessage().getTo());
        if (from == null && to == null) {
            logger.info("无关交易:{}", txEvent);
            return;
        }

        Order order = repository.findOrderByTxId(txEvent.getMessage().getCid().getStr());
        Order gasOrder = repository.findOrderByGasTxId(txEvent.getMessage().getCid().getStr());
        if (order != null) {
            if (!order.getStatus().equals(TransferStatus.GAS_OK)){
                logger.info("duplicated tx, cid:{}", txEvent.getMessage().getCid());
                return;
            }

            logger.info("收到transfer交易结果, cid:{}", txEvent.getMessage().getCid());
            MQTxMessage mq = MQTxMessage.builder()
                .reqId(order.getReqId())
                .chainName("filecoin")
                .tokenAddress("")
                .from(txEvent.getMessage().getFrom())
                .to(txEvent.getMessage().getTo())
                .value(txEvent.getMessage().getValue())
                .fee(txEvent.getReceipt().getGasUsed())
                .blockHeight(txEvent.getBlockHeight())
                .blockTime(new Date(txEvent.getBlockTime()))
                .build();

            if (txEvent.getReceipt().getExitCode().equals(ExitCode.Ok)) {
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
                logger.info("duplicated tx, cid:{}", txEvent.getMessage().getCid());
                return;
            }

            logger.info("收到gas转账交易结果, cid:{}", txEvent.getMessage().getCid());
            MQTxMessage mq = MQTxMessage.builder()
                .reqId(gasOrder.getReqId())
                .chainName("filecoin")
                .tokenAddress("")
                .from(txEvent.getMessage().getFrom())
                .to(txEvent.getMessage().getTo())
                .value(txEvent.getMessage().getValue())
                .fee(txEvent.getReceipt().getGasUsed())
                .blockHeight(txEvent.getBlockHeight())
                .blockTime(new Date(txEvent.getBlockTime()))
                .build();

            if (txEvent.getReceipt().getExitCode().equals(ExitCode.Ok)) {
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

            SignedMessage signedMessage = txm.sign(gasOrder.getTransfer(), gasOrder.getGasMessage());
            Cid cid = txm.send(signedMessage);
            gasOrder.setTxId(cid.getStr());
            repository.save(gasOrder);

            mq.setTransferTxId(cid.getStr());
            rocketMQTemplate.convertAndSend("filecoin", mq);
        } else if (to != null) {
            // 充值交易

        } else {
            logger.error("没有识别的交易:{}", txEvent);
        }
    }
}
