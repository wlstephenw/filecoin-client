package com.nenglian.filecoin.wallet.signer;

import com.nenglian.filecoin.rpc.domain.types.Message;
import com.nenglian.filecoin.rpc.domain.types.SignedMessage;

/**
 * @author stephen
 * @since 2021/7/15 上午11:33
 */

public class HSMSigner implements Signer {

    @Override
    public SignedMessage sign(Message transaction) {
        return null;
    }
}
