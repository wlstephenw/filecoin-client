package com.nenglian.filecoin.service;

import cn.hutool.core.util.HexUtil;
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
import com.nenglian.filecoin.transaction.dto.TxReceipt;
import com.nenglian.filecoin.wallet.Address;
import com.nenglian.filecoin.wallet.Wallet;
import java.io.IOException;
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
    TransferService transferService;
    @Autowired
    ReconciliationService reconciliationService;

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
        Result<String> res = new Result<>();
        String result = transferService.transfer(transfer);
        res.setData(result);
        res.setCode(0);
        return res;
    }


    @Override
    public Result<List<Reconciliation>> reconciliation(Date from, Date to) {
        return Result.<List<Reconciliation>>builder()
            .code(0)
            .data(reconciliationService.reconciliation(from, to))
            .build();
    }

    @Override
    public Result setPollHeight(Long fromHeight) {
        listener.getLatestBlock().getAndSet(fromHeight.intValue());
        return Result.builder().code(0).build();
    }

    @Override
    public Result<String> toAddress(String hexpk) {
        String address = new Address(Address.TestnetPrefix, HexUtil.decodeHex(hexpk)).toEncodedAddress();
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
            .time(new Date(head.getBlocks().get(0).getTimestamp() * 1000))
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

}
