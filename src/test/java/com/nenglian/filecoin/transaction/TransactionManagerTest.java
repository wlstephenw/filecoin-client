package com.nenglian.filecoin.transaction;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import cn.hutool.core.codec.Base64;
import com.nenglian.filecoin.rpc.api.LotusAPIFactory;
import com.nenglian.filecoin.rpc.api.LotusWalletAPI;
import com.nenglian.filecoin.rpc.cid.Cid.Codec;
import com.nenglian.filecoin.rpc.domain.MsgLookup;
import com.nenglian.filecoin.rpc.domain.cid.Cid;
import com.nenglian.filecoin.rpc.domain.types.KeyInfo;
import com.nenglian.filecoin.rpc.domain.types.SignedMessage;
import com.nenglian.filecoin.service.api.Transfer;
import com.nenglian.filecoin.service.api.WalletAddress;
import com.nenglian.filecoin.transaction.dto.TxEvent;
import com.nenglian.filecoin.wallet.Wallet;
import com.nenglian.filecoin.wallet.signer.NodeSigner;
import io.ipfs.multihash.Multihash.Type;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

/**
 * @author stephen
 * @since 2021/7/13 下午6:02
 */


class TransactionManagerTest {

    private final LotusWalletAPI lotusWalletAPI = LotusAPIFactory.create().createLotusWalletAPI();

    TransactionManager txManager = new TransactionManager(new Wallet());

    TransactionListener transactionListener = new TransactionListener();

    String sk = "YerlVcyqONmWWKE6bVAtQSgzogN4Vs9tq9/GhYxhsIk=";
    String pk = "f1f6giluhaka4myeah5hq4w4e6vt64pzwa74lsrsi";

    @BeforeEach
    public void setUp() {
        final Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        logger.setLevel(Level.INFO);
    }

    @Test
    public void testTransfer() throws IOException {
        Wallet wallet = txManager.getWallet();
        WalletAddress address = wallet.getWalletAddresFromSk(Base64.decode(sk));
        try {
            lotusWalletAPI.importKey(KeyInfo.builder().Type("secp256k1").PrivateKey(sk).build()).execute();
        }catch (Exception e){}


//        String from = address.getAddress();
        String from = "f1f6giluhaka4myeah5hq4w4e6vt64pzwa74lsrsi";
        String to = "f1oguiqe7dry7orwvriipthlwo57ojbr4tbxrexvq";

        int balance1 = txManager.lotusAPIFactory.createLotusWalletAPI().balance(to).execute().getResult().intValue();

        Transfer easyTransfer = Transfer.builder()
            .from(from)
            .to(to)
            .value(BigInteger.ONE)
            .build();

        SignedMessage signedMessage = txManager.sign(easyTransfer, null, new NodeSigner());
        Cid cid = txManager.send(signedMessage);

        System.out.println(signedMessage.getMessage().getCid());
        System.out.println(cid);
        com.nenglian.filecoin.rpc.cid.Cid cid1 = new TransactionSerializer().getCid(signedMessage.getMessage());
        System.out.println(cid1.toString());

        int balance = txManager.lotusAPIFactory.createLotusWalletAPI().balance(to).execute().getResult().intValue();
        Assert.assertTrue(balance > 0);
    }

    @Test
    public void testTransferFromNodeAddress() throws IOException {
        Wallet wallet = txManager.getWallet();
        WalletAddress address = wallet.getWalletAddresFromSk(Base64.decode(sk));

        String from = "f3upax7iznvckntkata6srcggnysr4vfltmh7aurxyhxmpahqpaxa6sjzy3uziyefjkb44lsozdp2snokloxqq";
        String to = address.getAddress();
//        String to = "f1sfy3qdptzgvfxdr6l3g4s23fwsceutd7g53k7ga";

        Transfer easyTransfer = Transfer.builder()
            .from(from)
            .to(to)
//            .value(new BigInteger("100000000000000000"))
            .value(new BigInteger("1"))
            .build();


        SignedMessage signedMessage = txManager.sign(easyTransfer, null, new NodeSigner());
        Cid cid = txManager.send(signedMessage);

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

        SignedMessage signedMessage = txManager.sign(easyTransfer, null, null);
        Cid cid = txManager.send(signedMessage);

        Thread.sleep(10 * 1000);

        MsgLookup lookup = transactionListener.getMsgLookup(cid);

        Map<Cid, TxEvent> cidTxEventMap = transactionListener.getMessagesFutureByHeight(lookup.getHeight() - 1).get();

        int balance2 = txManager.lotusAPIFactory.createLotusWalletAPI().balance(to).execute().getResult().intValue();
        Assert.assertTrue(balance2 > 0);
    }

}
