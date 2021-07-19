package com.nenglian.filecoin.service;

import cn.hutool.core.util.HexUtil;
import com.alibaba.fastjson.JSON;
import com.nenglian.filecoin.rpc.domain.cid.Cid;
import com.nenglian.filecoin.rpc.domain.types.Message;
import com.nenglian.filecoin.rpc.domain.types.TipSet;
import com.nenglian.filecoin.service.api.BlockInfo;
import com.nenglian.filecoin.service.api.EasyTransfer;
import com.nenglian.filecoin.service.api.Result;
import com.nenglian.filecoin.service.api.Reconciliation;
import com.nenglian.filecoin.service.api.Transfer;
import com.nenglian.filecoin.service.api.WalletAddress;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author stephen
 * @since 2021/7/13 下午5:04
 */
@RestController
public class Controller implements Api {

    @Autowired
    Wallet wallet;
    @Autowired
    OrderRepository repository;
    @Autowired
    TransactionListener listener;


    @Override
    public Result<WalletAddress> createAddress(String account, byte type) {
        repository.save( Order.builder().type("createAddress").build());
        WalletAddress address = wallet.createAddress();
        Result<WalletAddress> res = new Result<>();
        res.setCode(0);
        res.setData(address);
        return res;
    }

    @Override
    public Result<String> transfer(Transfer transfer) {
        TransactionManager txm = new TransactionManager(this.wallet);
        Order orderById = repository.findOrderById(transfer.getReqId());
        if (orderById != null){
            return Result.<String>builder().data(orderById.getTxId()).msg("duplicated order").build();
        }

        // 首先落库
        // TODO 先计算CID
        Order order = Order.builder().id(transfer.getReqId()).type("transfer").status("pending").build();
        repository.save(order);

        EasyTransfer easyTransfer = new EasyTransfer();
        BeanUtils.copyProperties(transfer, easyTransfer);
        Message gasMessage;
        Cid cid;
        if (!StringUtils.isEmpty(transfer.getGas())) {
            gasMessage = JSON.parseObject(transfer.getGas(), Message.class);
            cid = txm.easyTransfer(easyTransfer, gasMessage);
        }else {
            cid = txm.easyTransfer(easyTransfer);
        }

        order.setTxId(cid.getStr());
        order.setStatus("submitted");
        repository.save(order);

        Result<String> res = new Result<>();
        res.setCode(0);
        res.setData(cid.getStr());
        return res;
    }

    @Override
    public Result<String> gas(EasyTransfer transfer) {
        TransactionManager txm = new TransactionManager(this.wallet);
        Message message = txm.estimateGas(transfer);
        String gas = JSON.toJSONString(message);

        return Result.<String>builder().code(0).data(gas).build();
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
    public Result<String> toAddress(String hexpk) {
        String address = new Address(HexUtil.decodeHex(hexpk)).toEncodedAddress();
        return Result.<String>builder().code(0).data(address).build();
    }

    @Override
    public Result setPollHeight(Integer height) {
        listener.getLatestBlock().getAndSet(height);
        return Result.builder().code(0).build();
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
    public Result<BigDecimal> balance(String address) {

        BigInteger balance = wallet.balance(address);

        Result res = new Result();
        res.setCode(0);
        res.setData(Convert.fromAtto(balance));
        return res;
    }

    @EventListener
    public void handleTxEvent(TxEvent txEvent) {
        System.out.println("wallet receive txEvent, 补充gas已经到账，开始归集");
        Order order = repository.findOrderByTxId(txEvent.getMessage().getCid().getStr());
        if (order != null){
            order.setStatus("confirmed");
            repository.save(order);
        }
    }
}
