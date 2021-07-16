package com.nenglian.filecoin.service.db;

import java.math.BigInteger;
import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 * @author stephen
 * @since 2021/7/15 下午3:38
 */

@Data
public class Account {

    @Id
    String id;
    String pubKey;
    String address;
    BigInteger balance;
    String sk;
}
