package com.nenglian.filecoin.rpc.domain;



import com.nenglian.filecoin.rpc.domain.cid.Cid;
import com.nenglian.filecoin.rpc.domain.types.MessageReceipt;
import com.nenglian.filecoin.rpc.domain.types.TipSetKey;
import java.io.Serializable;
import lombok.Data;

/**
 * @author stephen
 */
@Data
public class MsgLookup implements Serializable {

    private Cid message;

    private MessageReceipt receipt;

    private Object returnDec;

    private TipSetKey tipSet;

    private Long height;
}
