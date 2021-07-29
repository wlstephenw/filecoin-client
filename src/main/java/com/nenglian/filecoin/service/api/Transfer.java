package com.nenglian.filecoin.service.api;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import lombok.Builder;
import lombok.Data;

/**
 * @author stephen
 * @since 2021/7/14 下午1:59
 */
@Data
@Builder
public class Transfer implements Serializable {

    /**
     * 请求ID
     */
    String reqId;

    /**
     * token合约地址
     */
    String tokenAddress;

    /**
     * GAS资产账户地址
     */
    String gasAddress;

    /**
     * 付款方地址
     */
    private String from;

    /**
     * 收款方地址
     */
    private String to;

    /**
     * 转账金额
     */
    private BigInteger value;

    /**
     * Gas加速因子
     */
    private BigDecimal gasSpeedUp;

    /**
     * 手续费是否包含在value内（仅对本币交易有意义）
     * <p>
     * true - value = 实际转账金额 + 手续费
     * false - value = 实际转账金额
     */
    private Boolean feeInclusive;

    Float speedup;
}
