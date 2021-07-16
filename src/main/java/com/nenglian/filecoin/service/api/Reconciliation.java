package com.nenglian.filecoin.service.api;

import java.math.BigDecimal;
import java.math.BigInteger;
import lombok.Builder;
import lombok.Data;

/**
 * @author stephen
 * @since 2021/7/16 下午12:08
 */

@Data
@Builder
public class Reconciliation {
    String txId;
    String from;
    String to;
    BigInteger value;
    BigInteger fee;
}
