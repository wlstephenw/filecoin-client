package com.nenglian.filecoin.rpc.domain;


import com.nenglian.filecoin.rpc.domain.cid.Cid;
import com.nenglian.filecoin.rpc.domain.types.ExecutionTrace;
import com.nenglian.filecoin.rpc.domain.types.MessageReceipt;
import java.io.Serializable;
import lombok.Data;

/**
 * @author stephen
 */
@Data
public class InvocResult implements Serializable {

    private Cid msgCid;

    private Message msg;

    private MessageReceipt msgRct;

    private MsgGasCost gasCost;

    private ExecutionTrace executionTrace;

    private String error;

    private Long duration;
}
