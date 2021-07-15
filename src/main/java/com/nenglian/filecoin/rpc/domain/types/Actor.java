package com.nenglian.filecoin.rpc.domain.types;

import com.nenglian.filecoin.rpc.domain.cid.Cid;
import java.io.Serializable;
import java.math.BigInteger;
import lombok.Data;

/**
 * @author stephen
 */
@Data
public class Actor implements Serializable {

    private Cid code;

    private Cid head;

    private Long nonce;

    private BigInteger balance;
}
