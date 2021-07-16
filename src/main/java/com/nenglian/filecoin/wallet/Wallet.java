package com.nenglian.filecoin.wallet;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.HexUtil;
import com.nenglian.filecoin.service.db.Account;
import com.nenglian.filecoin.service.db.AccountRepository;
import com.nenglian.filecoin.rpc.api.LotusAPIFactory;
import com.nenglian.filecoin.rpc.api.LotusChainAPI;
import com.nenglian.filecoin.transaction.dto.TxEvent;
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bouncycastle.math.ec.ECPoint;
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
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class WalletAddress{
        String pubKey;
        String address;
    }

    private static final String API_ROUTER = "http://localhost:7777/rpc/v1";
    private static final String AUTHORIZATION = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJBbGxvdyI6WyJyZWFkIl19.fNgcqyigMfozXVmBK13lhzPqDrjE3TwRDvcrwx9ReM0";
    protected LotusAPIFactory lotusAPIFactory = new LotusAPIFactory.Builder()
        .apiGateway(API_ROUTER)
        .authorization(AUTHORIZATION)
        .connectTimeout(5)
        .readTimeout(60)
        .writeTimeout(30)
        .build();
    LotusChainAPI lotusChainAPI = lotusAPIFactory.createLotusChainAPI();

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
            String address = new Address(pub).toEncodedAddress();
            WalletAddress addr = WalletAddress.builder().address(address).pubKey(HexUtil.encodeHexStr(pub)).build();
            db.put(addr.address, HexUtil.encodeHexStr(ecKeyPair.getPrivateKey().toByteArray()));

            Account account = new Account();
            account.setAddress(addr.address);
            account.setPubKey(addr.pubKey);
            account.setBalance(BigInteger.ZERO);
            account.setSk(HexUtil.encodeHexStr(ecKeyPair.getPrivateKey().toByteArray()));
            repository.save(account);

            return addr;
        } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }
        return null;
    }

    public WalletAddress importBase64Key(String base64SK){
        byte[] priv = Base64.decode(base64SK);
        return importkey(priv);
    }

    public WalletAddress importHexKey(String hexSK){
        byte[] priv = Base64.decode(hexSK);
        return importkey(priv);
    }

    private WalletAddress importkey(byte[] priv) {
        byte[] pub = Sign.publicPointFromPrivate(Numeric.toBigInt(priv)).getEncoded(false);
        String address = new Address(pub).toEncodedAddress();
        WalletAddress addr = WalletAddress.builder().address(address).pubKey(HexUtil.encodeHexStr(pub)).build();
        db.put(addr.address, HexUtil.encodeHexStr(priv));
        Account account = new Account();
        account.setAddress(addr.address);
        account.setPubKey(addr.pubKey);
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
            return new NodeSigner();
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
    public void handleTxEvent(TxEvent txEvent) {
        System.out.println("wallet receive txEvent, update balance");
        System.out.println(txEvent);
        Account from = repository.findAccountByAddress(txEvent.getMessage().getFrom());
        if (from != null){
            from.setBalance(from.getBalance().subtract(txEvent.getMessage().getValue()));
            repository.save(from);
        }
        Account to = repository.findAccountByAddress(txEvent.getMessage().getTo());
        if (to != null){
            to.setBalance(to.getBalance().add(txEvent.getMessage().getValue()));
            repository.save(to);
        }
    }


}
