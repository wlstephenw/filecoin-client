package com.nenglian.filecoin.rpc.domain;

import com.nenglian.filecoin.rpc.domain.types.TipSet;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @author stephen
 */
@Data
public class ActiveSyncs implements Serializable {

    private Long workerID;

    private TipSet base;

    private TipSet target;

    private SyncStateStage stage;

    private Long height;

    private Date start;

    private Date end;

    private String message;
}
