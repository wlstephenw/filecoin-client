package com.nenglian.filecoin.transaction;

import co.nstant.in.cbor.CborBuilder;
import co.nstant.in.cbor.CborEncoder;
import co.nstant.in.cbor.model.ByteString;
import co.nstant.in.cbor.model.DataItem;
import co.nstant.in.cbor.model.UnsignedInteger;

import com.nenglian.filecoin.rpc.domain.types.Message;
import com.nenglian.filecoin.wallet.Address;
import com.nenglian.filecoin.wallet.FilecoinCnt;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import ove.crypto.digest.Blake2b;

public class TransactionSerializer {

    /**
     * 获取CID hash
     *
     * @param transaction 交易实体
     */
    public byte[] transactionSerialize(Message transaction) throws  IOException {
        int versions = 0;
        ByteString fromByte = new ByteString(Address.from(transaction.getFrom()).getBytes());
        ByteString toByte = new ByteString(Address.from(transaction.getTo()).getBytes());
        UnsignedInteger versionByte = new UnsignedInteger(versions);
        UnsignedInteger nonceByte = new UnsignedInteger(transaction.getNonce());
        byte[] valueBytes = transaction.getValue().toByteArray();
        valueBytes = WriteMajorTypeHeaderBuf(valueBytes, FilecoinCnt.MajUnsignedInt, transaction.getValue().toString());
        ByteString valueByte = new ByteString(valueBytes);
        byte[] gasFeeCapBytes = transaction.getGasFeeCap().toByteArray();
        gasFeeCapBytes = WriteMajorTypeHeaderBuf(gasFeeCapBytes, FilecoinCnt.MajUnsignedInt, transaction.getGasFeeCap().toString());
        ByteString gasFeeCapByte = new ByteString(gasFeeCapBytes);

        byte[] gasPeremiumBytes = transaction.getGasPremium().toByteArray();
        gasPeremiumBytes = WriteMajorTypeHeaderBuf(gasPeremiumBytes, FilecoinCnt.MajUnsignedInt, transaction.getGasPremium().toString());
        ByteString gasPeremiumByte = new ByteString(gasPeremiumBytes);

        UnsignedInteger gasLimitByte = new UnsignedInteger(transaction.getGasLimit());

        UnsignedInteger methodByte = new UnsignedInteger(transaction.getMethod());

        ByteString paramsByte = new ByteString(new byte[]{});

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] cidHash = null;
        try {
            CborEncoder encoder = new CborEncoder(baos);
            List<DataItem> build = new CborBuilder().addArray()
                    .add(versionByte)
                    .add(toByte)
                    .add(fromByte)
                    .add(nonceByte)
                    .add(valueByte)
                    .add(gasLimitByte)
                    .add(gasFeeCapByte)
                    .add(gasPeremiumByte)
                    .add(methodByte)
                    .add(paramsByte)
                    .end().build();
            encoder.encode(build);
            byte[] encodedBytes = baos.toByteArray();
            cidHash = getCidHash(encodedBytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            baos.close();
        }
        return cidHash;
    }

    /**
     * 形成摘要需要拼接的字符串
     */
    private byte[] CID_PREFIX = new byte[]{0x01, 0x71, (byte) 0xa0, (byte) 0xe4, 0x02, 0x20};

    /**
     * @param message 交易结构体的序列化字节
     *                通过交易结构体字节获取CidHash
     */
    public byte[] getCidHash(byte[] message) {
        Blake2b.Param param = new Blake2b.Param();
        param.setDigestLength(32);

        //消息体字节
        byte[] messageByte = Blake2b.Digest.newInstance(param).digest(message);

        int xlen = CID_PREFIX.length;
        int ylen = messageByte.length;

        byte[] result = new byte[xlen + ylen];

        System.arraycopy(CID_PREFIX, 0, result, 0, xlen);
        System.arraycopy(messageByte, 0, result, xlen, ylen);

        return Blake2b.Digest.newInstance(param).digest(result);
    }


    private byte[] WriteMajorTypeHeaderBuf(byte[] bytes, int c, String value) {
        if (bytes[0] != 0) {
            byte[] buf = new byte[bytes.length + 1];
            buf[0] = (byte) c;
            System.arraycopy(bytes, 0, buf, 1, bytes.length);
            return buf;
        }
        return bytes;
    }
}
