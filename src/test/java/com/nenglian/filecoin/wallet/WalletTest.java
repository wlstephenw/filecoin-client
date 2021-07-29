package com.nenglian.filecoin.wallet;


import cn.hutool.core.lang.Assert;
import com.nenglian.filecoin.service.api.WalletAddress;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author stephen
 * @since 2021/7/13 下午6:02
 */

@SpringBootTest
class WalletTest {

    final static String sk = "YerlVcyqONmWWKE6bVAtQSgzogN4Vs9tq9/GhYxhsIk=";
    final static String pk = "t1f6giluhaka4myeah5hq4w4e6vt64pzwa74lsrsi";

    @Autowired
    Wallet wallet;
    @Test
    void createAddress() {
        WalletAddress address = wallet.createAddress();
        Assert.notNull(address);
    }


    @Test
    public void importBase64Key() {
        String address = wallet.importBase64Key(sk).getAddress();
        Assert.isTrue(address.equals(pk));
    }
}
