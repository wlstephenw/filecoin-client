package com.nenglian.filecoin.service;

import com.nenglian.filecoin.transaction.dto.TxEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @author stephen
 * @since 2021/7/15 下午3:24
 */

@Component
public class MQSender {

    @EventListener
    public void handleTxEvent(TxEvent txEvent) {
        System.out.println("MQSender tx event, send to mq");
        System.out.println(txEvent);
    }

}
