package com.nenglian.filecoin.rpc.domain;


import com.nenglian.filecoin.rpc.domain.builtin.SectorInfo;
import com.nenglian.filecoin.rpc.domain.types.BeaconEntry;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;
import lombok.Data;

/**
 * @author stephen
 */
@Data
public class MiningBaseInfo implements Serializable {

    private BigInteger minerPower;

    private BigInteger networkPower;

    private List<SectorInfo> sectors;

    private String workerKey;

    private Long sectorSize;

    private BeaconEntry prevBeaconEntry;

    private List<BeaconEntry> beaconEntries;

    private Boolean eligibleForMining;
}
