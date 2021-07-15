package com.nenglian.filecoin.rpc.api;

import cn.hutool.core.util.HexUtil;
import com.nenglian.filecoin.rpc.domain.types.KeyInfo;
import com.nenglian.filecoin.rpc.jasonrpc.Response;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Base64;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author stephen
 */
public class LotusWalletAPITest extends AbstractLotusAPITest {

    private final LotusWalletAPI lotusWalletAPI = lotusAPIFactory.createLotusWalletAPI();

    @Test
    public void balance() throws IOException {
        Response<String> defaultAddress = lotusWalletAPI.defaultAddress().execute();
        Response<BigInteger> response = lotusWalletAPI.balance(defaultAddress.getResult()).execute();
        Assert.assertNotNull(response.getResult());
    }

    @Test
    public void defaultAddress() throws IOException {
        Response<String> response = lotusWalletAPI.defaultAddress().execute();
        Assert.assertNotNull(response.getResult());
    }

    @Test
    public void list() throws IOException{
        Response<List<String>> response = lotusWalletAPI.list().execute();
        Assert.assertNotNull(response.getResult());
    }

    @Test
    public void create()throws IOException {
        Response<String> response = lotusWalletAPI.create("secp256k1").execute();
        lotusWalletAPI.export(response.getResult()).execute();
        Assert.assertNotNull(response.getResult());
    }

    @Test
    public void export() throws IOException {
        Response<String> defaultAddress = lotusWalletAPI.defaultAddress().execute();
        Response<KeyInfo> response = lotusWalletAPI.export(defaultAddress.getResult()).execute();
        byte[]  bytes = Base64.getDecoder().decode(response.getResult().getPrivateKey());
        System.out.println(HexUtil.encodeHex(bytes));
        Assert.assertNotNull(response.getResult());
    }

    @Test
    public void importKey() throws IOException {
        String key = "13QW/y6qBRl8yGz5vlyqamVopSvejPBQjeBFZtRFls8=";
        Response<String> response = lotusWalletAPI.importKey(KeyInfo.builder().Type("secp256k1").PrivateKey(key).build()).execute();
        Assert.assertNotNull(response.getResult());
    }

}
