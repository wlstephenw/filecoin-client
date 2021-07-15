package com.nenglian.filecoin.api;

import com.nenglian.filecoin.api.Api;
import com.nenglian.filecoin.api.MessageResult;
import com.nenglian.filecoin.db.Order;
import com.nenglian.filecoin.db.OrderRepository;
import com.nenglian.filecoin.rpc.domain.cid.Cid;
import com.nenglian.filecoin.transaction.TransactionManager;
import com.nenglian.filecoin.transaction.dto.EasyTransfer;
import com.nenglian.filecoin.transaction.dto.TxEvent;
import com.nenglian.filecoin.wallet.Convert;
import com.nenglian.filecoin.wallet.Wallet;
import com.nenglian.filecoin.wallet.Wallet.WalletAddress;
import java.math.BigDecimal;
import java.math.BigInteger;
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
    public MessageResult transfer(String from, String to, String value) {



        TransactionManager txm = new TransactionManager(this.wallet);
        Cid cid = txm.easyTransfer(EasyTransfer.builder().from(from).to(to).value(Convert.toAtto(value)).build());

        repository.save( Order.builder().type("transfer").txId(cid.getStr()).build());

        MessageResult res = new MessageResult();
        res.setCode(0);
        res.setObject(cid);
        return res;
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
        Order order = repository.findOrderByTxId(txEvent.getCid().getStr());
        if (order != null){
            order.setStatus("done");
            repository.save(order);
        }
    }
}
