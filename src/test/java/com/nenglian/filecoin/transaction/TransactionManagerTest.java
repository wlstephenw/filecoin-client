package com.nenglian.filecoin.transaction;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import cn.hutool.core.codec.Base64;
import com.nenglian.filecoin.rpc.api.LotusAPIFactory;
import com.nenglian.filecoin.rpc.api.LotusWalletAPI;
import com.nenglian.filecoin.rpc.domain.InvocResult;
import com.nenglian.filecoin.rpc.domain.MsgLookup;
import com.nenglian.filecoin.rpc.domain.cid.Cid;
import com.nenglian.filecoin.rpc.domain.exitcode.ExitCode;
import com.nenglian.filecoin.rpc.domain.types.KeyInfo;
import com.nenglian.filecoin.rpc.domain.types.Message;
import com.nenglian.filecoin.rpc.domain.types.SignedMessage;
import com.nenglian.filecoin.service.api.Transfer;
import com.nenglian.filecoin.service.api.WalletAddress;
import com.nenglian.filecoin.transaction.dto.TxReceipt;
import com.nenglian.filecoin.wallet.Address;
import com.nenglian.filecoin.wallet.Wallet;
import com.nenglian.filecoin.wallet.signer.NodeSigner;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

/**
 * @author stephen
 * @since 2021/7/13 下午6:02
 */


class TransactionManagerTest {

    LotusAPIFactory lotusAPIFactory = LotusAPIFactory.create();
    private final LotusWalletAPI lotusWalletAPI = lotusAPIFactory.createLotusWalletAPI();
    TransactionManager txManager = new TransactionManager(new Wallet());
    TransactionListener transactionListener = new TransactionListener();

    final String sk = "YerlVcyqONmWWKE6bVAtQSgzogN4Vs9tq9/GhYxhsIk=";
    final String pk = "f1f6giluhaka4myeah5hq4w4e6vt64pzwa74lsrsi";

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

        SignedMessage signedMessage = txManager.sign(easyTransfer, null, new NodeSigner(lotusAPIFactory));
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

        String from = "t1xsr4q5ojvby5dcj44sjdx5td45maewzzr2n4zpi";
//        String from = "f3vuewltmtk4umj7mgahptsrngwtl4lcivu3tv4zwrbxgo4qa2lwwznxvirh4y3rqztrqnwb4zyf2nnr3hbywa";
        String to = address.getAddress();

        Transfer easyTransfer = Transfer.builder()
            .from(from)
            .to(to)
            .value(new BigInteger("1"))
            .build();


        SignedMessage signedMessage = txManager.sign(easyTransfer, null, new NodeSigner(lotusAPIFactory));
        MsgLookup msgLookup = txManager.sendAndWait(signedMessage);
        Assert.assertTrue(msgLookup.getReceipt().getExitCode().equals(ExitCode.Ok));

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

        Map<Cid, TxReceipt> cidTxEventMap = transactionListener.getMessagesFutureByHeight(lookup.getHeight() - 1).get();

        int balance2 = txManager.lotusAPIFactory.createLotusWalletAPI().balance(to).execute().getResult().intValue();
        Assert.assertTrue(balance2 > 0);
    }


    @Test
    public void testGasEstimation() throws IOException {
        byte[] pub = Sign.publicPointFromPrivate(Numeric.toBigInt(Base64.decode(sk))).getEncoded(false);
        String address = new Address(Address.TestnetPrefix, pub).toEncodedAddress();

        String from = "t1xsr4q5ojvby5dcj44sjdx5td45maewzzr2n4zpi";
        String to = address;

        Transfer transfer = Transfer.builder()
            .from(from)
            .to(to)
            .value(new BigInteger("1"))
            .build();

        Message gasMessage = txManager.estimateGas(transfer);
        gasMessage = txManager.txSpeedup(gasMessage, 1.0f);
        BigInteger before = lotusWalletAPI.balance(from).execute().getResult();

        SignedMessage signedMessage = txManager.sign(transfer, gasMessage, new NodeSigner(lotusAPIFactory));

        Message message = signedMessage.getMessage();
        System.out.println("message.getGasLimit() = " + message.getGasLimit());
        System.out.println("message.getGasFeeCap() = " + message.getGasFeeCap());
        System.out.println("message.getGasPremium() = " + message.getGasPremium());

        MsgLookup msgLookup = txManager.sendAndWait(signedMessage);
        Assert.assertEquals(msgLookup.getReceipt().getExitCode(), ExitCode.Ok);


        BigInteger after = lotusWalletAPI.balance(from).execute().getResult();
        BigInteger diff = before.subtract(after);

        System.out.println("gasUsed() = " + msgLookup.getReceipt().getGasUsed());
        System.out.println("diff = " + diff);

        BigInteger getGasPremiumMgetGasLimit = message.getGasPremium().multiply(new BigInteger(message.getGasLimit().toString()));
        BigInteger getGasFeeCapMgetGasLimit = message.getGasFeeCap().multiply(new BigInteger(message.getGasLimit().toString()));
        System.out.println("getGasPremium * getGasLimit = " + getGasPremiumMgetGasLimit);
        System.out.println("getGasFeeCap * getGasLimit = " + getGasFeeCapMgetGasLimit);

        InvocResult invocResult = lotusAPIFactory.createLotusStateAPI().replay(null, message.getCid()).execute().getResult();
        System.out.println("invocResult" + invocResult);

        Assert.assertEquals(invocResult.getGasCost().getTotalCost(), diff.subtract(BigInteger.ONE));

    }

}
