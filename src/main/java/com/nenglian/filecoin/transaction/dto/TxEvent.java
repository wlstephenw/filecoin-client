package com.nenglian.filecoin.transaction.dto;

import com.nenglian.filecoin.rpc.domain.types.Message;
import com.nenglian.filecoin.rpc.domain.types.MessageReceipt;
import java.util.Date;
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
    Message message;
    MessageReceipt receipt;
    Long blockHeight;
    Long blockTime;
}
