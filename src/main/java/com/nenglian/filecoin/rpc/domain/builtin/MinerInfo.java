package com.nenglian.filecoin.rpc.domain.builtin;

import com.nenglian.filecoin.rpc.domain.abi.RegisteredSealProof;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * @author stephen
 */
@Data
public class MinerInfo implements Serializable {

    private String minerId;

    private String owner;

    private String worker;

    private String newWorker;

    private List<String> controlAddresses;

    private Long workerChangeEpoch;

    private String peerId;

    private List<String> multiaddrs;

    private RegisteredSealProof sealProofType;

    private Long sectorSize;

    private Long windowPoStPartitionSectors;

    private Long consensusFaultElapsed;
}
