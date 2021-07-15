package com.nenglian.filecoin.rpc.domain.cid;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import lombok.Data;

/**
 * @author stephen
 */
@Data
public class Cid implements Serializable {

    @JsonProperty("/")
    private String str;

    public static Cid of(String str) {
        Cid result = new Cid();
        result.str = str;
        return result;
    }
}
