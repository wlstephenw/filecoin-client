package com.nenglian.filecoin.rpc.domain.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * @author stephen
 */
@Data
public class GasTrace implements Serializable {

    private String name;

    @JsonProperty("loc")
    private List<Loc> location;

    @JsonProperty("tg")
    private Long totalGas;

    @JsonProperty("cg")
    private Long computeGas;

    @JsonProperty("sg")
    private Long storageGas;

    @JsonProperty("vtg")
    private Long totalVirtualGas;

    @JsonProperty("vcg")
    private Long virtualComputeGas;

    @JsonProperty("vsg")
    private Long virtualStorageGas;

    @JsonProperty("tt")
    private Long timeTaken;

    @JsonProperty("ex")
    private Object extra;
}
