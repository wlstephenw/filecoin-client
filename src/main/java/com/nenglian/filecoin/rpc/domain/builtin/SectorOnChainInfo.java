package com.nenglian.filecoin.rpc.domain.builtin;

import com.nenglian.filecoin.rpc.domain.*;
import com.nenglian.filecoin.rpc.domain.abi.RegisteredSealProof;
import com.nenglian.filecoin.rpc.domain.cid.Cid;
import com.nenglian.filecoin.rpc.domain.types.*;
import com.nenglian.filecoin.rpc.jasonrpc.*;
import java.math.BigInteger;
import java.util.List;
import lombok.Data;

/**
 * @author stephen
 */
@Data
public class SectorOnChainInfo {

    private Long sectorNumber;

    private RegisteredSealProof sealProof;

    private Cid sealedCID;

    private List<Long> dealIDs;

    private Long activation;

    private Long expiration;

    private BigInteger dealWeight;

    private BigInteger verifiedDealWeight;

    private BigInteger initialPledge;

    private BigInteger expectedDayReward;

    private BigInteger expectedStoragePledge;
}
