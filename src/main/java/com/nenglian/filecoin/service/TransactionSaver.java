package com.nenglian.filecoin.service;

import com.nenglian.filecoin.service.db.Tx;
import com.nenglian.filecoin.service.db.TxRepository;
import com.nenglian.filecoin.transaction.dto.TxReceipt;
import com.nenglian.filecoin.wallet.Wallet;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @author stephen
 * @since 2021/7/15 下午3:32
 */

@Component
public class TransactionSaver {

    @Autowired
    TxRepository repository;
    @Autowired
    Wallet wallet;

    @EventListener
    public void handleTxEvent(TxReceipt txReceipt) {

        if (!wallet.isOurs(txReceipt))
            return;

        Tx tx = repository.findTxByCid(txReceipt.getCid().getStr());
        if (null != tx)
            return;

        tx = new Tx();
        tx.setCid(txReceipt.getCid().getStr());
        BeanUtils.copyProperties(txReceipt.getMessage(), tx);
        BeanUtils.copyProperties(txReceipt.getReceipt(), tx);
        BeanUtils.copyProperties(txReceipt.getInvocResult().getGasCost(), tx);
        BeanUtils.copyProperties(txReceipt, tx);
        repository.save(tx);
    }

    public Long getLargestHeigt(){
        List<Tx> list = repository.findFirstByOrderByBlockHeightDesc();
        if (list.size() == 0)
            return null;
        return list.get(0).getBlockHeight();
    }

}
