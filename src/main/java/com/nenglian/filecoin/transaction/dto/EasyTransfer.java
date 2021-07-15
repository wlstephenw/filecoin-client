package com.nenglian.filecoin.transaction.dto;

import java.io.Serializable;
import java.math.BigInteger;
import lombok.Builder;
import lombok.Data;

/**
 * @author stephen
 * @since 2021/7/14 下午1:59
 */

@Data
@Builder
public class EasyTransfer implements Serializable {

    /**
     * 转账方地址
     */
    private String from;
    /**
     * 收账方地址
     */
    private String to;
    /**
     * 转账金额
     */
    private BigInteger value;
    /**
     * 私钥
     */
    private String privatekey;
}


