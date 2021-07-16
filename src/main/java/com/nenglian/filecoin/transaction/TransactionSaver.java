package com.nenglian.filecoin.transaction;

import com.nenglian.filecoin.service.db.TxRepository;
import com.nenglian.filecoin.transaction.dto.TxEvent;
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
    @EventListener
    public void handleTxEvent(TxEvent txEvent) {
        System.out.println("wallet receive txEvent, 保存到数据库");
        repository.save(txEvent.getMessage());
    }
}
