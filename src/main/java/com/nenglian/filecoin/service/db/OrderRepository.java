package com.nenglian.filecoin.service.db;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author stephen
 * @since 2021/7/15 下午3:42
 */

public interface OrderRepository extends MongoRepository<Order, String> {

     Order findOrderByTxId(String txId);
}
