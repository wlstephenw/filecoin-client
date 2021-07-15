package com.nenglian.filecoin.rpc.domain.types;


import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * @author stephen
 */
@Data
public class MpoolConfig implements Serializable {

    private List<String> priorityAddrs;

    private Integer sizeLimitHigh;

    private Integer sizeLimitLow;

    private Double replaceByFeeRatio;

    /**
     * 单位：ns
     */
    private Long pruneCooldown;

    private Double gasLimitOverestimation;
}
