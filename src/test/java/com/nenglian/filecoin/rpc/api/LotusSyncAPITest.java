package com.nenglian.filecoin.rpc.api;

import com.nenglian.filecoin.rpc.domain.*;
import com.nenglian.filecoin.rpc.jasonrpc.*;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author stephen
 */
public class LotusSyncAPITest extends AbstractLotusAPITest {

    private final LotusSyncAPI lotusSyncAPI = lotusAPIFactory.createLotusSyncAPI();

    @Test
    public void state() throws IOException {
        Response<SyncState> response = lotusSyncAPI.state().execute();
        Assert.assertNotNull(response.getResult());
    }
}
