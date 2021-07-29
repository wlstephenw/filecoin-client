package com.nenglian.filecoin.service;

import com.nenglian.filecoin.service.db.Account;
import com.nenglian.filecoin.transaction.dto.TxReceipt;
import com.nenglian.filecoin.wallet.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;

/**
 * @author stephen
 * @since 2021/7/28 下午8:30
 */

public class RechargeService {

    @Autowired
    Wallet wallet;

    @EventListener
    public void handleTxEvent(TxReceipt txReceipt) {

        if (!wallet.isOurs(txReceipt))
            return;

        Account to = wallet.get(txReceipt.getMessage().getTo());
        if (to != null){
            // TODO 充值处理

        }
    }

}
