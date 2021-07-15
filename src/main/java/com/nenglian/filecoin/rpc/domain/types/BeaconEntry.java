package com.nenglian.filecoin.rpc.domain.types;

import java.io.Serializable;
import lombok.Data;

/**
 * @author stephen
 */
@Data
public class BeaconEntry implements Serializable {

    private Long round;

    private String data;
}
