package com.nenglian.filecoin.service.api;

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
public class Transfer implements Serializable {

    String reqId;

    String tokenAddress;

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

    // 从estimatedGas方法返回的，不需要了解具体类型，回传即可
    String gas;
}


