package com.nenglian.filecoin.transaction;

import com.nenglian.filecoin.rpc.api.LotusAPIFactory;
import com.nenglian.filecoin.rpc.api.LotusStateAPI;
import com.nenglian.filecoin.rpc.domain.MsgLookup;
import com.nenglian.filecoin.rpc.domain.cid.Cid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author stephen
 * @since 2021/7/27 上午11:10
 */

public class ReceiptProcessor {
    private static final Logger logger = LoggerFactory.getLogger(ReceiptProcessor.class);

    private final long sleepDuration;
    private final int attempts;
    LotusAPIFactory apiFactory;

    public ReceiptProcessor(LotusAPIFactory apiFactory, long sleepDuration, int attempts) {
        this.apiFactory = apiFactory;
        this.sleepDuration = sleepDuration;
        this.attempts = attempts;
    }

    public MsgLookup waitForTransactionReceipt(
        String transactionHash) {

        return getTransactionReceipt(Cid.of(transactionHash), sleepDuration, attempts);
    }

    private MsgLookup getTransactionReceipt(
        Cid cid, long sleepDuration, int attempts)
         {

        MsgLookup msgLookup =
            sendTransactionReceiptRequest(cid);
        for (int i = 0; i < attempts; i++) {
            if (null == msgLookup) {
                try {
                    Thread.sleep(sleepDuration);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                msgLookup = sendTransactionReceiptRequest(cid);
            } else {
                return msgLookup;
            }
        }

        throw new RuntimeException("Transaction receipt was not generated after "
            + ((sleepDuration * attempts) / 1000
            + " seconds for transaction: " + cid));
    }

    private MsgLookup sendTransactionReceiptRequest(Cid cid){

        LotusStateAPI lotusStateAPI = apiFactory.createLotusStateAPI();
        MsgLookup msgLookup = null;
        try {
            msgLookup = lotusStateAPI
                .searchMsg(cid).execute().getResult();
            logger.info("the txid:{} 's receipt is: {}", cid, msgLookup);
        } catch (Exception e) {
            logger.debug("get receipt error:", e);
        }

        return msgLookup;

    }

}
