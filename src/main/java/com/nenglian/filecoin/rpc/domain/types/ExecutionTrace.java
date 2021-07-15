package com.nenglian.filecoin.rpc.domain.types;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * @author stephen
 */
@Data
public class ExecutionTrace implements Serializable {

    private Message msg;

    private MessageReceipt msgRct;

    private String error;

    private Long duration;

    private List<GasTrace> gasCharges;

    private List<ExecutionTrace> subcalls;
}
