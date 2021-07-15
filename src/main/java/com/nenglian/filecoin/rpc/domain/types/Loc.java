package com.nenglian.filecoin.rpc.domain.types;

import java.io.Serializable;
import lombok.Data;

/**
 * @author stephen
 */
@Data
public class Loc implements Serializable {

    private String file;

    private Integer line;

    private String function;
}
