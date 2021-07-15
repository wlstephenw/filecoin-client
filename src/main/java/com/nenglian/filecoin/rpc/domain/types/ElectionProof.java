package com.nenglian.filecoin.rpc.domain.types;


import java.io.Serializable;
import lombok.Data;

/**
 * @author stephen
 */
@Data
public class ElectionProof implements Serializable {

    private Long winCount;

    private String vRFProof;
}
