package com.nenglian.filecoin.rpc.domain;

import java.io.Serializable;
import java.math.BigInteger;
import lombok.Data;

/**
 * @author stephen
 */
@Data
public class ActorState implements Serializable {

    private BigInteger balance;

    // 	State   interface{}
    private Object state;
}
