package com.nenglian.filecoin.rpc.domain;


import java.math.BigInteger;
import lombok.Data;

/**
 * @author stephen
 */
@Data
public class MessageSendSpec {

    private BigInteger maxFee;
}
