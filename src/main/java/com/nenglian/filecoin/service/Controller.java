package com.nenglian.filecoin.service;

import cn.hutool.core.util.HexUtil;
import com.nenglian.filecoin.rpc.domain.types.Message;
import com.nenglian.filecoin.service.api.MessageResult;
import com.nenglian.filecoin.service.api.Reconciliation;
import com.nenglian.filecoin.service.db.Order;
import com.nenglian.filecoin.service.db.OrderRepository;
import com.nenglian.filecoin.rpc.domain.cid.Cid;
import com.nenglian.filecoin.transaction.TransactionListener;
import com.nenglian.filecoin.transaction.TransactionManager;
import com.nenglian.filecoin.service.api.EasyTransfer;
import com.nenglian.filecoin.transaction.dto.TxEvent;
import com.nenglian.filecoin.wallet.Address;
import com.nenglian.filecoin.wallet.Convert;
import com.nenglian.filecoin.wallet.Wallet;
import com.nenglian.filecoin.wallet.Wallet.WalletAddress;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
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
    public MessageResult getNewAddress(String account, byte type) {

        repository.save( Order.builder().type("getNewAddress").build());

        WalletAddress address = wallet.createAddress();

        MessageResult res = new MessageResult();
        res.setCode(0);
        res.setObject(address);
        return res;
    }

    @Override
    public MessageResult transfer(EasyTransfer transfer) {
        TransactionManager txm = new TransactionManager(this.wallet);
        Cid cid = txm.easyTransfer(transfer);
        repository.save(Order.builder().id(transfer.getId()).type("transfer").txId(cid.getStr()).build());
        MessageResult res = new MessageResult();
        res.setCode(0);
        res.setObject(cid);
        return res;
    }


    @Override
    public MessageResult reconciliation(Date from, Date to) {
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

            return MessageResult.builder().code(0).object(collect).build();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public MessageResult toAddress(String hexpk) {
        String address = new Address(HexUtil.decodeHex(hexpk)).toEncodedAddress();
        return MessageResult.builder().code(0).object(address).build();
    }

    @Override
    public MessageResult setPollHeight(Integer height) {
        listener.getLatestBlock().getAndSet(height);
        return MessageResult.builder().code(0).build();
    }

    @Override
    public MessageResult collect(String fromAddress, String toAddress, String gasAddress, String collectId) {
        MessageResult res = new MessageResult();
        res.setCode(0);
        res.setObject("this is fake, will fix soon");
        return res;
    }

    @Override
    public MessageResult withdraw(String toAddress, BigDecimal amount, BigDecimal fee, Boolean isSync,
        String withdrawId) {
        MessageResult res = new MessageResult();
        res.setCode(0);
        res.setObject("this is fake, will fix soon");
        return res;
    }


    @Override
    public MessageResult blockHeight() {
        MessageResult res = new MessageResult();
        res.setCode(0);
        res.setObject("this is fake, will fix soon");
        return res;
    }

    @Override
    public MessageResult balance(String address) {

        BigInteger balance = wallet.balance(address);

        MessageResult res = new MessageResult();
        res.setCode(0);
        res.setObject(Convert.fromAtto(balance));
        return res;
    }

    @EventListener
    public void handleTxEvent(TxEvent txEvent) {
        System.out.println("wallet receive txEvent, 补充gas已经到账，开始归集");
        Order order = repository.findOrderByTxId(txEvent.getMessage().getCid().getStr());
        if (order != null){
            order.setStatus("done");
            repository.save(order);
        }
    }
}
