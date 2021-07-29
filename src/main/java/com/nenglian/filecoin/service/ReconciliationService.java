package com.nenglian.filecoin.service;

import com.nenglian.filecoin.rpc.domain.cid.Cid;
import com.nenglian.filecoin.service.api.Reconciliation;
import com.nenglian.filecoin.service.api.Result;
import com.nenglian.filecoin.service.db.Tx;
import com.nenglian.filecoin.service.db.TxRepository;
import com.nenglian.filecoin.transaction.TransactionListener;
import com.nenglian.filecoin.transaction.dto.TxReceipt;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author stephen
 * @since 2021/7/28 下午12:15
 */

@Service
public class ReconciliationService {

    @Autowired
    TransactionListener listener;
    @Autowired
    TxRepository txRepository;

    public List<Reconciliation> reconciliation(Date from, Date to) {
        List<Tx> txes = txRepository
            .findTxesByBlockTimeIsBetween(from.getTime() / 1000, to.getTime() / 1000);

        List<Reconciliation> reconciliations = txes.stream().map(e -> {
            return Reconciliation
                .builder()
                .txId(e.getCid())
                .from(e.getFrom())
                .to(e.getTo())
                .value(e.getValue())
                .fee(e.getTotalCost())
                .blockHeight(e.getBlockHeight())
                .blockTime(new Date(e.getBlockTime() * 1000))
                .build();
        }).collect(Collectors.toList());
        return reconciliations;
    }

//    public List<Reconciliation> reconciliation(Date from, Date to) {
//        try {
//            CompletableFuture<Map<Cid, TxReceipt>> future = listener
//                .getMessagesFutureByHeightRange(from, to);
//            Map<Cid, TxReceipt> map = future.get();
//            List<Reconciliation> collect = map.entrySet().stream()
//                .map((e) -> Reconciliation.builder()
//                    .txId(e.getKey().getStr())
//                    .from(e.getValue().getMessage().getFrom())
//                    .to(e.getValue().getMessage().getTo())
//                    .value(e.getValue().getMessage().getValue())
//                    .fee(e.getValue().getInvocResult().getGasCost().getTotalCost())
//                    .build())
//                .collect(Collectors.toList());
//            return collect;
//        }catch (Exception e){
//            throw new RuntimeException(e);
//        }
//    }

}
