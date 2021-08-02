package com.nenglian.filecoin.service.api;

import java.math.BigInteger;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author stephen
 * @since 2021/7/19 上午11:03
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MQTxMessage {
    String reqId;
    String txId;
    String chainName;
    String tokenAddress;
    String from;
    String to;
    BigInteger value;
    BigInteger fee;
    Long blockHeight;
    Date blockTime;
    Byte status;
    String errorMsg;
}
