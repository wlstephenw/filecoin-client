package com.nenglian.filecoin.rpc.api;


import com.nenglian.filecoin.rpc.domain.types.BeaconEntry;
import com.nenglian.filecoin.rpc.jasonrpc.Response;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author stephen
 */
public class LotusBeaconAPITest extends AbstractLotusAPITest {

    private final LotusBeaconAPI lotusBeaconAPI = lotusAPIFactory.createLotusBeaconAPI();

    @Test
    public void getEntry() throws IOException {
        Response<BeaconEntry> response = lotusBeaconAPI.getEntry(445113L).execute();
        Assert.assertNotNull(response.getResult());
    }
}
