package com.nenglian.filecoin.rpc.domain.builtin;

import com.nenglian.filecoin.rpc.domain.*;
import com.nenglian.filecoin.rpc.domain.abi.RegisteredSealProof;
import com.nenglian.filecoin.rpc.domain.cid.Cid;
import com.nenglian.filecoin.rpc.domain.types.*;
import com.nenglian.filecoin.rpc.jasonrpc.*;
import java.io.Serializable;
import lombok.Data;

/**
 * @author stephen
 */
@Data
public class SectorInfo implements Serializable {

    private RegisteredSealProof sealProof;

    private Long sectorNumber;

    private Cid sealedCID;
}
