package com.nenglian.filecoin.transaction.dto;

import com.nenglian.filecoin.rpc.domain.cid.Cid;
import com.nenglian.filecoin.rpc.domain.types.Message;
import java.math.BigInteger;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * @author stephen
 * @since 2021/7/15 下午3:20
 */

@Data
@Builder
@ToString
public class TxEvent {
    Cid cid;
    String from;
    String to;
    BigInteger value;
    BigInteger fee;
    Message message;
}
