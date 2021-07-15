package com.nenglian.filecoin.rpc.domain.proof;

import com.nenglian.filecoin.rpc.domain.abi.RegisteredPoStProof;
import java.io.Serializable;
import lombok.Data;

/**
 * @author stephen
 */
@Data
public class PoStProof implements Serializable {

    private RegisteredPoStProof poStProof;

    private String proofBytes;
}
