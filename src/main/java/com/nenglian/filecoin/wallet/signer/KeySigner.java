package com.nenglian.filecoin.wallet.signer;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.HexUtil;
import com.nenglian.filecoin.rpc.domain.cid.Cid;
import com.nenglian.filecoin.rpc.domain.crypto.SigType;
import com.nenglian.filecoin.rpc.domain.crypto.Signature;
import com.nenglian.filecoin.rpc.domain.types.Message;
import com.nenglian.filecoin.rpc.domain.types.SignedMessage;
import com.nenglian.filecoin.transaction.TransactionSerializer;
import org.web3j.crypto.ECKeyPair;

/**
 * @author stephen
 * @since 2021/7/15 上午10:12
 */

public class KeySigner implements Signer {

    byte[] privateKey;
    private TransactionSerializer transactionSerializer = new TransactionSerializer();

    public KeySigner(String privateKey) {
        this.privateKey = HexUtil.decodeHex(privateKey);
    }

    public KeySigner(byte[] privateKey) {
        this.privateKey = privateKey;
    }

    @Override
    public SignedMessage sign(Message transaction) {

        byte[] cidHash = null;
        try {
            transaction.setCid(Cid.of(transactionSerializer.getCid(transaction).toString()));
            cidHash = transactionSerializer.getCidHash(transaction);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("transaction entity serialization failed");
        }

        //签名
        ECKeyPair ecKeyPair = ECKeyPair.create(this.privateKey);
        org.web3j.crypto.Sign.SignatureData signatureData = org.web3j.crypto.Sign.signMessage(cidHash,
            ecKeyPair, false);


        Signature signature = new Signature();
        signature.setType(SigType.SigTypeSecp256k1);
        signature.setData( Base64.encode(getSignature(signatureData)));

        SignedMessage signedMessage = new SignedMessage();
        signedMessage.setMessage(transaction);
        signedMessage.setSignature(signature);
        return signedMessage;
    }

    private static byte[] getSignature(org.web3j.crypto.Sign.SignatureData signatureData) {
        byte[] sig = new byte[65];
        System.arraycopy(signatureData.getR(), 0, sig, 0, 32);
        System.arraycopy(signatureData.getS(), 0, sig, 32, 32);
        sig[64] = (byte) ((signatureData.getV() & 0xFF) - 27);//为啥减去27看signMessage（）方法（内部源码）
        return sig;
    }

}
