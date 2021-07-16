package com.nenglian.filecoin.transaction;


import com.nenglian.filecoin.wallet.Wallet;
import com.nenglian.filecoin.wallet.Wallet.WalletAddress;
import com.nenglian.filecoin.service.api.EasyTransfer;
import java.io.IOException;
import java.math.BigInteger;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

/**
 * @author stephen
 * @since 2021/7/13 下午6:02
 */

class TransactionManagerTest {

    String sk = "YerlVcyqONmWWKE6bVAtQSgzogN4Vs9tq9/GhYxhsIk=";
    String pk = "f1f6giluhaka4myeah5hq4w4e6vt64pzwa74lsrsi";

    @Test
    void createAddress() {
    }

    @Test
    public void easyTransfer() throws IOException {
        Wallet wallet = new Wallet();
        TransactionManager txManager = new TransactionManager(wallet);

        WalletAddress address = wallet.importBase64Key(sk);

        String from = address.getAddress();
        String to = "f1oguiqe7dry7orwvriipthlwo57ojbr4tbxrexvq";

        int balance1 = txManager.lotusAPIFactory.createLotusWalletAPI().balance(to).execute().getResult().intValue();

        EasyTransfer easyTransfer = EasyTransfer.builder()
            .from(from)
            .to(to)
            .value(BigInteger.ONE)
            .build();

        txManager.easyTransfer(easyTransfer);

        int balance = txManager.lotusAPIFactory.createLotusWalletAPI().balance(to).execute().getResult().intValue();
        Assert.assertTrue(balance > 0);
    }

    @Test
    public void easyTransferNodeAddress() throws IOException {
        Wallet wallet = new Wallet();
        TransactionManager txManager = new TransactionManager(wallet);
        WalletAddress address = wallet.importBase64Key(sk);

        String from = "f3vrbv2uouhcci3kzlstfmhbewbjmvhieom3iqegpaok4mj6nollgut7w4qmbqa6wyrsgy5myzkcjrodkd2urq";
        String to = address.getAddress();

        EasyTransfer easyTransfer = EasyTransfer.builder()
            .from(from)
            .to(to)
            .value(new BigInteger("10000000000000"))
            .build();

        txManager.easyTransfer(easyTransfer);



        int balance = txManager.lotusAPIFactory.createLotusWalletAPI().balance(to).execute().getResult().intValue();
        Assert.assertTrue(balance > 0);
    }
}
