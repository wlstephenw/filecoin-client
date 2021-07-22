package com.nenglian.filecoin.transaction;


import com.nenglian.filecoin.rpc.domain.MsgLookup;
import com.nenglian.filecoin.rpc.domain.cid.Cid;
import com.nenglian.filecoin.rpc.domain.types.MessageReceipt;
import com.nenglian.filecoin.rpc.domain.types.SignedMessage;
import com.nenglian.filecoin.service.api.Transfer;
import com.nenglian.filecoin.service.api.WalletAddress;
import com.nenglian.filecoin.transaction.dto.TxEvent;
import com.nenglian.filecoin.wallet.Wallet;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author stephen
 * @since 2021/7/13 下午6:02
 */

@SpringBootTest
class TransactionManagerTest {

    @Autowired
    TransactionManager txManager;

    TransactionListener transactionListener = new TransactionListener();

    String sk = "YerlVcyqONmWWKE6bVAtQSgzogN4Vs9tq9/GhYxhsIk=";
    String pk = "f1f6giluhaka4myeah5hq4w4e6vt64pzwa74lsrsi";

    @Test
    void createAddress() {
    }

    @Test
    public void testTransfer() throws IOException {
        Wallet wallet = txManager.getWallet();
        WalletAddress address = wallet.importBase64Key(sk);

        String from = address.getAddress();
        String to = "f1oguiqe7dry7orwvriipthlwo57ojbr4tbxrexvq";

        int balance1 = txManager.lotusAPIFactory.createLotusWalletAPI().balance(to).execute().getResult().intValue();

        Transfer easyTransfer = Transfer.builder()
            .from(from)
            .to(to)
            .value(BigInteger.ONE)
            .build();

        SignedMessage sign = txManager.sign(easyTransfer);
        Cid cid = txManager.send(sign);
        Assert.assertEquals(cid.getStr(), sign.getMessage().getCid().getStr());

        int balance = txManager.lotusAPIFactory.createLotusWalletAPI().balance(to).execute().getResult().intValue();
        Assert.assertTrue(balance > 0);
    }

    @Test
    public void testTransferFromNodeAddress() throws IOException {
        Wallet wallet = txManager.getWallet();
        WalletAddress address = wallet.importBase64Key(sk);

        String from = "f3upax7iznvckntkata6srcggnysr4vfltmh7aurxyhxmpahqpaxa6sjzy3uziyefjkb44lsozdp2snokloxqq";
//        String to = address.getAddress();
        String to = "f1sfy3qdptzgvfxdr6l3g4s23fwsceutd7g53k7ga";

        Transfer easyTransfer = Transfer.builder()
            .from(from)
            .to(to)
            .value(new BigInteger("100000000000000000"))
            .build();

        txManager.signAndSend(easyTransfer);

        int balance = txManager.lotusAPIFactory.createLotusWalletAPI().balance(to).execute().getResult().intValue();
        Assert.assertTrue(balance > 0);
    }


    @Test
    public void testTransferFromNodeAddressAndListen() throws IOException, ExecutionException, InterruptedException {
        Wallet wallet = txManager.getWallet();
        WalletAddress address = wallet.importBase64Key(sk);

        String from = "f3upax7iznvckntkata6srcggnysr4vfltmh7aurxyhxmpahqpaxa6sjzy3uziyefjkb44lsozdp2snokloxqq";
        String to = address.getAddress();

        int balance1 = txManager.lotusAPIFactory.createLotusWalletAPI().balance(to).execute().getResult().intValue();

        Transfer easyTransfer = Transfer.builder()
            .from(from)
            .to(to)
            .value(new BigInteger("1"))
            .build();

        Cid cid = txManager.signAndSend(easyTransfer);

        Thread.sleep(10 * 1000);

        MsgLookup lookup = transactionListener.getMsgLookup(cid);

        Map<Cid, TxEvent> cidTxEventMap = transactionListener.getMessagesFutureByHeight(lookup.getHeight() - 1).get();

        int balance2 = txManager.lotusAPIFactory.createLotusWalletAPI().balance(to).execute().getResult().intValue();
        Assert.assertTrue(balance2 > 0);
    }

}
