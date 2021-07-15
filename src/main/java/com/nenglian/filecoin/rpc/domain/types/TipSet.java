package com.nenglian.filecoin.rpc.domain.types;


import com.nenglian.filecoin.rpc.domain.cid.Cid;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * @author stephen
 */
@Data
public class TipSet implements Serializable {

    private List<Cid> cids;

    private List<BlockHeader> blocks;

    private Long height;
}
