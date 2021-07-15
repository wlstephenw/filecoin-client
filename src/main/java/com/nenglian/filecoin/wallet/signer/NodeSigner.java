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
    private static final String API_ROUTER = "http://localhost:7777/rpc/v1";
    private static final String AUTHORIZATION = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJBbGxvdyI6WyJyZWFkIl19.fNgcqyigMfozXVmBK13lhzPqDrjE3TwRDvcrwx9ReM0";

    protected LotusAPIFactory lotusAPIFactory = new LotusAPIFactory.Builder()
        .apiGateway(API_ROUTER)
        .authorization(AUTHORIZATION)
        .connectTimeout(5)
        .readTimeout(60)
        .writeTimeout(30)
        .build();

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
