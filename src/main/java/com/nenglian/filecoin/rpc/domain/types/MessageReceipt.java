package com.nenglian.filecoin.rpc.domain.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nenglian.filecoin.rpc.domain.exitcode.ExitCode;
import java.io.Serializable;
import java.math.BigInteger;
import lombok.Data;

/**
 * @author stephen
 */
@Data
public class MessageReceipt implements Serializable {

    private ExitCode exitCode;

    @JsonProperty("Return")
    private String ret;

    private BigInteger gasUsed;
}
