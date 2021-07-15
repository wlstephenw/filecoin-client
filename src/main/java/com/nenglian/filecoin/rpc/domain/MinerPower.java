package com.nenglian.filecoin.rpc.domain;



import com.nenglian.filecoin.rpc.domain.builtin.Claim;
import java.io.Serializable;
import lombok.Data;

/**
 * @author stephen
 */
@Data
public class MinerPower implements Serializable {

    private Claim minerPower;

    private Claim totalPower;

    private Boolean hasMinPower;
}
