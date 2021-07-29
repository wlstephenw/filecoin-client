package com.nenglian.filecoin.wallet;

import cn.hutool.core.codec.Base32;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import java.io.Serializable;
import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ove.crypto.digest.Blake2b;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Address implements Serializable {

    // MainnetPrefix is the main network prefix.
    public static final String MainnetPrefix = "f";
    // TestnetPrefix is the test network prefix.
    public static final String TestnetPrefix = "t";

    private byte type;
    private String network;
    private byte[] bytes;

    /**
     * secp256k1地址字节长度
     */
    private final static int addressLength = 20;

    public Address(String network, byte[] pub) {
        if(pub.length < 65){
            throw new RuntimeException("Only support 65 bytes public key");
        }
        this.network = network;
        // TODO use bytes to do all these staff
        Blake2b.Digest digest = Blake2b.Digest.newInstance(20);
        this.type = 1;
        this.bytes = digest.digest(pub);
    }

    public static Address from(String addressStr) {
        if (StrUtil.isBlank(addressStr)) {
            throw new NullPointerException("addressStr 参数不能为空");
        }
        //去掉拼接的前两位
        String substring = addressStr.substring(2);
        //获取type
        String typeStr = addressStr.substring(1, 2);
        //获取网络类型
        String network = addressStr.substring(0, 1);
        int type = Integer.parseInt(typeStr);
        if (type != 1 && type != 2 && type != 3) {
            throw new RuntimeException("错误的地址类型");
        }
        switch (type) {
            case 1:
                //secp256k1 地址类型
                break;
            case 2:
            case 3:
                throw new RuntimeException("暂不支持的地址类型");
            default:
                throw new RuntimeException("错误地址类型");
        }

        byte[] bytes = Arrays.copyOf(Base32.decode(substring), addressLength);
        // TODO check checksum

        return Address.builder().type((byte) type).bytes(bytes).network(network).build();
    }

    // get encoded bytes, network id + raw bytes
    public byte[] getBytes(){
        byte[] dest = new byte[this.bytes.length + 1];
        // secp256k1 id
        dest[0] = 1;
        System.arraycopy(this.bytes, 0, dest, 1, this.bytes.length);
        return dest;
    }

    public byte[] getRawBytes(){
        return this.bytes;
    }

    public String toEncodedAddress(){
        Blake2b.Digest blake2b3 = Blake2b.Digest.newInstance(4);
        byte[] checksum = blake2b3.digest(this.getBytes());
        byte[] dest = new byte[this.getRawBytes().length + checksum.length];
        System.arraycopy(this.getRawBytes(), 0, dest, 0, this.getRawBytes().length);
        System.arraycopy(checksum, 0, dest, this.getRawBytes().length, checksum.length);

        return this.network + String.valueOf(this.type) + Base32.encode(dest).toLowerCase();
    }

}
