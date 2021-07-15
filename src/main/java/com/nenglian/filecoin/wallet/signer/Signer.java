package com.nenglian.filecoin.wallet.signer;

import com.nenglian.filecoin.rpc.domain.types.Message;
import com.nenglian.filecoin.rpc.domain.types.SignedMessage;

/**
 * @author stephen
 * @since 2021/7/15 上午10:21
 */

public interface Signer {

     SignedMessage sign(Message transaction);
}
