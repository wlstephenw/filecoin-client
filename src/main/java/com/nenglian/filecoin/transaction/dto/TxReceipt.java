package com.nenglian.filecoin.transaction.dto;

import com.nenglian.filecoin.rpc.domain.InvocResult;
import com.nenglian.filecoin.rpc.domain.MsgGasCost;
import com.nenglian.filecoin.rpc.domain.cid.Cid;
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
public class TxReceipt {
    Cid cid;
    Message message;
    MessageReceipt receipt;
    InvocResult invocResult;
    Long blockHeight;
    Long blockTime;
}
