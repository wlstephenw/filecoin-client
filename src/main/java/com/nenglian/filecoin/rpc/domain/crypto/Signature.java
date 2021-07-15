package com.nenglian.filecoin.rpc.domain.crypto;

import java.io.Serializable;
import lombok.Data;

/**
 * @author stephen
 */
@Data
public class Signature implements Serializable {

    private SigType type;

    private String data;
}
