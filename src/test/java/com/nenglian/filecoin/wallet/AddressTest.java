package com.nenglian.filecoin.wallet;

import cn.hutool.core.codec.Base64;
import com.nenglian.filecoin.rpc.api.AbstractLotusAPITest;
import com.nenglian.filecoin.wallet.Address;
import java.io.IOException;
import java.util.Arrays;
import org.bouncycastle.math.ec.ECPoint;
import org.junit.Assert;
import org.junit.Test;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

/**
 * @author stephen
 * @since 2021/7/14 下午3:11
 */

public class AddressTest extends AbstractLotusAPITest {

    @Test
    public void byteToAddress() {
        byte[] bytes = new byte[32];
        Arrays.fill(bytes, (byte) 1);

        // 必须要使用65位的公钥
        ECPoint ecPoint = Sign.publicPointFromPrivate(Numeric.toBigInt(bytes));
        byte[] pub = ecPoint.getEncoded(false);
        String localAddress = new Address(pub).toEncodedAddress();

        String lotusAddress = "f1ksu3ktw4xhyaoltwr546b3epfs5wxxqfyyxipwi";
        Assert.assertEquals(localAddress, lotusAddress);
    }

    @Test
    public void byteToAddress1() {
        String base64 = "YerlVcyqONmWWKE6bVAtQSgzogN4Vs9tq9/GhYxhsIk=";
        byte[] priv = Base64.decode(base64);
        byte[] pub = Sign.publicPointFromPrivate(Numeric.toBigInt(priv)).getEncoded(false);
        String localAddress = new Address(pub).toEncodedAddress();

        String lotusAddress = "f1f6giluhaka4myeah5hq4w4e6vt64pzwa74lsrsi";
        Assert.assertEquals(localAddress, lotusAddress);
    }

    @Test
    public void testAddressImport() throws IOException {
    }
}
