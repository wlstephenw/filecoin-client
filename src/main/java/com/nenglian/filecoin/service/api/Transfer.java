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
    private Float speedUp;

    String gasData;
}
