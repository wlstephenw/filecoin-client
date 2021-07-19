package com.nenglian.filecoin.wallet.signer;

import com.nenglian.filecoin.rpc.api.LotusAPIFactory;
import com.nenglian.filecoin.rpc.api.LotusGasAPI;
import com.nenglian.filecoin.rpc.domain.cid.Cid;
import com.nenglian.filecoin.rpc.domain.types.Message;
import com.nenglian.filecoin.rpc.domain.types.SignedMessage;
import com.nenglian.filecoin.rpc.jasonrpc.Response;
import java.io.IOException;

/**
 * @author stephen
 * @since 2021/7/15 上午10:30
 */

public class NodeSigner implements Signer {
    protected LotusAPIFactory lotusAPIFactory = LotusAPIFactory.create();
    private final LotusGasAPI lotusGasAPI = lotusAPIFactory.createLotusGasAPI();

    @Override
    public SignedMessage sign(Message message) {
        SignedMessage signedMessage = null;
        try {
            signedMessage = lotusAPIFactory.createLotusWalletAPI().signMessage(message.getFrom(), message)
                .execute().getResult();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return signedMessage;
    }
}
