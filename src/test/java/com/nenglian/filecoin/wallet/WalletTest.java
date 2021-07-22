package com.nenglian.filecoin.wallet;


import cn.hutool.core.lang.Assert;
import com.nenglian.filecoin.service.api.WalletAddress;
import org.junit.jupiter.api.Test;

/**
 * @author stephen
 * @since 2021/7/13 下午6:02
 */

class WalletTest {
    Wallet wallet = new Wallet();
    @Test
    void createAddress() {
        WalletAddress address = wallet.createAddress();
        Assert.notNull(address);
    }




}
