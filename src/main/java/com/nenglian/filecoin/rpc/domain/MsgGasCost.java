package com.nenglian.filecoin.rpc.domain;


import com.nenglian.filecoin.rpc.domain.cid.Cid;
import java.io.Serializable;
import java.math.BigInteger;
import lombok.Data;

/**
 * @author stephen
 */
@Data
public class MsgGasCost implements Serializable {

    private Cid message;

    private BigInteger gasUsed;

    private BigInteger baseFeeBurn;

    private BigInteger overEstimationBurn;

    private BigInteger minerPenalty;

    private BigInteger minerTip;

    private BigInteger refund;

    private BigInteger totalCost;
}
