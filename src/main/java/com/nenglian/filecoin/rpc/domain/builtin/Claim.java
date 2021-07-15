package com.nenglian.filecoin.rpc.domain.builtin;

import java.io.Serializable;
import lombok.Data;

/**
 * @author stephen
 */
@Data
public class Claim implements Serializable {

    // Sum of raw byte power for a miner's sectors.
    private Long rawBytePower;

    // Sum of quality adjusted power for a miner's sectors.
    private Long qualityAdjPower;
}
