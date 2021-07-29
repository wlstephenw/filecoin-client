package com.nenglian.filecoin.service.db;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author stephen
 * @since 2021/7/15 下午3:42
 */

public interface TxRepository extends MongoRepository<Tx, String> {

     Tx findTxByCid(String cid);

     List<Tx> findFirstByOrderByBlockHeightDesc();

     List<Tx> findTxesByBlockTimeIsBetween(Long from, Long to);
}
