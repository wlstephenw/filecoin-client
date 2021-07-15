package com.nenglian.filecoin.rpc.domain.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nenglian.filecoin.rpc.domain.cid.Cid;
import java.io.Serializable;
import java.math.BigInteger;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author stephen
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Message implements Serializable {

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

    @JsonProperty("CID")
    private Cid cid;
}
