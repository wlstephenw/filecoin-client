package com.nenglian.filecoin.rpc.domain.types;


import java.io.Serializable;
import lombok.Data;

/**
 * @author stephen
 */
@Data
public class Ticket implements Serializable {

    private String vRFProof;
}
