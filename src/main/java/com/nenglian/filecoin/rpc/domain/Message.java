package com.nenglian.filecoin.rpc.domain;

import com.nenglian.filecoin.rpc.domain.cid.Cid;
import lombok.Data;

/**
 * @author stephen
 */
@Data
public class Message {

    private Cid cid;

    private com.nenglian.filecoin.rpc.domain.types.Message message;
}
