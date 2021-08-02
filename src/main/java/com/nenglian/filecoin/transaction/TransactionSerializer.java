package com.nenglian.filecoin.transaction;

import cn.hutool.core.codec.Base64;
import co.nstant.in.cbor.CborBuilder;
import co.nstant.in.cbor.CborEncoder;
import co.nstant.in.cbor.model.ByteString;
import co.nstant.in.cbor.model.DataItem;
import co.nstant.in.cbor.model.UnsignedInteger;

import com.nenglian.filecoin.rpc.cid.Cid;
import com.nenglian.filecoin.rpc.cid.Cid.Codec;
import com.nenglian.filecoin.rpc.domain.crypto.Signature;
import com.nenglian.filecoin.rpc.domain.types.Message;
import com.nenglian.filecoin.rpc.domain.types.SignedMessage;
import com.nenglian.filecoin.wallet.Address;
import com.nenglian.filecoin.wallet.FilecoinCnt;
import io.ipfs.multihash.Multihash.Type;
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
    public byte[] getCidHash(Message transaction) throws  IOException {
        Cid cid = this.getCid(transaction);
        Blake2b.Param param = new Blake2b.Param();
        param.setDigestLength(32);
        byte[] hash = Blake2b.Digest.newInstance(param).digest(cid.toBytes());
        return hash;
    }

    public Cid getCid(Message transaction) {
        try {
            byte[] serialize = this.serialize(transaction);
            return getCid(serialize);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public Cid getCid(SignedMessage signedMessage) {
        try {
            byte[] serialize = this.serialize(signedMessage);
            return getCid(serialize);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    public Cid getCid(byte[] serialize) {
        Blake2b.Param param = new Blake2b.Param();
        param.setDigestLength(32);
        byte[] hash = Blake2b.Digest.newInstance(param).digest(serialize);
        return Cid
            .buildCidV1(Codec.DagCbor, Type.blake2b_256, hash);
    }


    public byte[] serialize(Signature signature) throws  IOException{

        byte type = (byte) signature.getType().getCode();
        byte[] bytes = Base64.decode(signature.getData());
        byte[] buf = new byte[bytes.length + 1];
        buf[0] = type;
        System.arraycopy(bytes, 0, buf, 1, bytes.length);

        ByteString sigBytes = new ByteString(buf);
        ByteArrayOutputStream outBuff = new ByteArrayOutputStream();
        try {
            CborEncoder encoder = new CborEncoder(outBuff);
            List<DataItem> build = new CborBuilder().add(sigBytes).build();
            encoder.encode(build);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            outBuff.close();
        }
        return outBuff.toByteArray();
    }


    public byte[] serialize(SignedMessage signedMessage) throws  IOException {
        byte[] msg = this.serialize(signedMessage.getMessage());
        byte[] sig = this.serialize(signedMessage.getSignature());

        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        buf.write((byte) 130);
        buf.write(msg);
        buf.write(sig);
        byte[] bytes = buf.toByteArray();
        return bytes;
    }

    public byte[] serialize(Message transaction) throws  IOException {
        int versions = 0;
        if (transaction.getVersion() != null){
            versions = transaction.getVersion().intValue();
        }
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
        byte[] encodedBytes = null;
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
            encodedBytes = baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            baos.close();
        }
        return encodedBytes;
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
