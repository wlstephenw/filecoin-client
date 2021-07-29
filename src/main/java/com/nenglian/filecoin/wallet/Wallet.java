package com.nenglian.filecoin.wallet;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.HexUtil;
import com.nenglian.filecoin.service.api.WalletAddress;
import com.nenglian.filecoin.service.db.Account;
import com.nenglian.filecoin.service.db.AccountRepository;
import com.nenglian.filecoin.rpc.api.LotusAPIFactory;
import com.nenglian.filecoin.transaction.dto.TxReceipt;
import com.nenglian.filecoin.wallet.signer.KeySigner;
import com.nenglian.filecoin.wallet.signer.NodeSigner;
import com.nenglian.filecoin.wallet.signer.Signer;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.HashMap;
import java.util.List;
import org.bouncycastle.math.ec.ECPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

/**
 * @author stephen
 * @since 2021/7/13 下午5:43
 */

@Component
public class Wallet {
    private static final Logger logger = LoggerFactory.getLogger(Wallet.class);


    protected LotusAPIFactory lotusAPIFactory = LotusAPIFactory.create();

    // TODO 模拟数据库
    HashMap<String, String> db = new HashMap<>();

    @Autowired
    AccountRepository repository;

    public WalletAddress createAddress(){
        try {
            // TODO 重写一下
            ECKeyPair ecKeyPair = Keys.createEcKeyPair();
            ECPoint ecPoint = Sign.publicPointFromPrivate(ecKeyPair.getPrivateKey());
            byte[] pub = ecPoint.getEncoded(false);
            String address = new Address(Address.TestnetPrefix, pub).toEncodedAddress();
            WalletAddress addr = WalletAddress.builder().address(address).pubKey(HexUtil.encodeHexStr(pub)).build();
            db.put(addr.getAddress(), HexUtil.encodeHexStr(ecKeyPair.getPrivateKey().toByteArray()));

            Account account = new Account();
            account.setAddress(addr.getAddress());
            account.setPubKey(addr.getPubKey());
            account.setBalance(BigInteger.ZERO);
            account.setSk(HexUtil.encodeHexStr(ecKeyPair.getPrivateKey().toByteArray()));
            repository.save(account);

            return addr;
        } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String createNodeAddress(){
        try {
            return lotusAPIFactory.createLotusWalletAPI().create("secp256k1").execute().getResult();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public WalletAddress importBase64Key(String base64SK){
        byte[] priv = Base64.decode(base64SK);
        return importkey(priv);
    }

    public WalletAddress importHexKey(String hexSK){
        byte[] priv = HexUtil.decodeHex(hexSK);
        return importkey(priv);
    }

    public WalletAddress getWalletAddresFromSk(byte[] priv){
        byte[] pub = Sign.publicPointFromPrivate(Numeric.toBigInt(priv)).getEncoded(false);
        String address = new Address(Address.TestnetPrefix, pub).toEncodedAddress();
        return WalletAddress.builder().address(address).pubKey(HexUtil.encodeHexStr(pub)).build();
    }

    private WalletAddress importkey(byte[] priv) {
        WalletAddress addr = this.getWalletAddresFromSk(priv);
        Account account = repository.findAccountByAddress(addr.getAddress());
        if (account != null){
            return addr;
        }
        db.put(addr.getAddress(), HexUtil.encodeHexStr(priv));
        account = new Account();
        account.setAddress(addr.getAddress());
        account.setPubKey(addr.getPubKey());
        account.setBalance(BigInteger.ZERO);
        account.setSk(HexUtil.encodeHexStr(priv));
        repository.save(account);
        return addr;
    }

    public Signer getSigner(String address){
        Account account = repository.findAccountByAddress(address);
        if (account != null)
            return new KeySigner(account.getSk());
        else
            return new NodeSigner(this.lotusAPIFactory);
    }

    public List<WalletAddress> list(){
        return null;
    }

    public BigInteger balance(String address){
        try {
            return lotusAPIFactory.createLotusWalletAPI().balance(address).execute().getResult();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @EventListener
    public void handleTxEvent(TxReceipt txReceipt) {
//        Account from = repository.findAccountByAddress(txEvent.getMessage().getFrom());
//        if (from != null){
//            from.setBalance(from.getBalance().subtract(txEvent.getMessage().getValue()));
//            logger.info("update balance for: {}, balance:{}", from.getAddress(), from.getBalance());
//            repository.save(from);
//        }
//        Account to = repository.findAccountByAddress(txEvent.getMessage().getTo());
//        if (to != null){
//            to.setBalance(to.getBalance().add(txEvent.getMessage().getValue()));
//            logger.info("update balance for: {}, balance:{}", to.getAddress(), to.getBalance());
//            repository.save(to);
//        }
    }

    public Account get(String address){
        return repository.findAccountByAddress(address);
    }

    public boolean isOurs(TxReceipt txReceipt){
        Account from = this.get(txReceipt.getMessage().getFrom());
        Account to = this.get(txReceipt.getMessage().getTo());
        if (from == null && to == null) {
            return false;
        }
        return true;
    }


}
