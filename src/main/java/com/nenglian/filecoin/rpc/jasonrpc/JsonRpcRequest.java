package com.nenglian.filecoin.rpc.jasonrpc;

import java.io.Serializable;
import lombok.Builder;
import lombok.Data;

/**
 * @author stephen
 */
@Builder
@Data
public final class JsonRpcRequest implements Serializable {

    private long id;

    // 2.0
    private String jsonrpc;

    private String method;

    private Object params;
}
