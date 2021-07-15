package com.nenglian.filecoin.rpc.domain;


import java.io.Serializable;
import lombok.Data;

/**
 * @author stephen
 */
@Data
public class ObjStat implements Serializable {

    private long size;

    private long links;
}
