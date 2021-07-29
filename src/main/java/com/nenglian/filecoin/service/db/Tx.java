package com.nenglian.filecoin.service.db;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nenglian.filecoin.rpc.domain.cid.Cid;
import com.nenglian.filecoin.rpc.domain.exitcode.ExitCode;
import java.math.BigInteger;
import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 * @author stephen
 * @since 2021/7/28 下午5:07
 */

@Data
public class Tx {
    private Long version;
    private String to;
    private String from;
    private Long nonce;
    private BigInteger value;
    private Long gasLimit;
    /**
     * 用户选择支付的总手续费率
     */
    private BigInteger gasFeeCap;
    /**
     * 用户选择支付给矿工的手续费率
     */
    private BigInteger gasPremium;
    private Long method;
    private String params;
    @Id
    private String cid;

    private ExitCode exitCode;
    private String ret;
    private BigInteger gasUsed;


    private BigInteger baseFeeBurn;
    private BigInteger overEstimationBurn;
    private BigInteger minerPenalty;
    private BigInteger minerTip;
    private BigInteger refund;
    private BigInteger totalCost;

    private Long blockHeight;
    private Long blockTime;
}
