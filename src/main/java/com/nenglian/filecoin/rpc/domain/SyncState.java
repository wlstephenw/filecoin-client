package com.nenglian.filecoin.rpc.domain;


import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * @author stephen
 */
@Data
public class SyncState implements Serializable {

    private List<ActiveSyncs> activeSyncs;

    private Long vMApplied;
}
