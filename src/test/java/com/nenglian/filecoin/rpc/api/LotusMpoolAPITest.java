package com.nenglian.filecoin.rpc.api;

import com.nenglian.filecoin.rpc.jasonrpc.*;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author stephen
 */
public class LotusMpoolAPITest extends AbstractLotusAPITest {

    private final LotusMpoolAPI lotusMPoolAPI = lotusAPIFactory.createLotusMPoolAPI();

    @Test
    public void getNonce() throws IOException {
        Response<Long> response = lotusMPoolAPI.getNonce("f1rfw5ln22fw63llzqyhjrgmx572j5hquyvymmrpq").execute();
        Assert.assertNotNull(response.getResult());
    }

    @Test
    public void push() {
    }

    @Test
    public void testPush() {
    }

    @Test
    public void pending() {
    }
}
