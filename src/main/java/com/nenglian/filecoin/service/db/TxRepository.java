package com.nenglian.filecoin.service.db;

import com.nenglian.filecoin.rpc.domain.types.Message;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author stephen
 * @since 2021/7/15 下午3:42
 */

public interface TxRepository extends MongoRepository<Message, String> {

     Message findMessageByCid_Str(String cid);
}
