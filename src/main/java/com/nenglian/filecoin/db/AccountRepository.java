package com.nenglian.filecoin.db;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author stephen
 * @since 2021/7/15 下午3:42
 */

public interface AccountRepository extends MongoRepository<Account, String> {

    public Account findAccountByAddress(String address);
}
