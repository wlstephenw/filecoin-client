package com.nenglian.filecoin.rpc.api;

import com.nenglian.filecoin.rpc.domain.MiningBaseInfo;
import com.nenglian.filecoin.rpc.domain.types.*;
import com.nenglian.filecoin.rpc.jasonrpc.*;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author stephen
 */
public class LotusMinerAPITest extends AbstractLotusAPITest {

    private final LotusMinerAPI lotusMinerAPI = lotusAPIFactory.createLotusMinerAPI();

    @Test
    public void getBaseInfo() throws IOException {
        String address = "f065266";
        long chainEpoch = 438131L;
        TipSetKey tsk = TipSetKey.of("bafy2bzacedssauumzfkfiohefymyrf7zc2mdotsu66vbvs7lgyjqhk3s5ro4g");

        Response<MiningBaseInfo> response = lotusMinerAPI.getBaseInfo(address, chainEpoch, tsk).execute();
        Assert.assertNotNull(response.getResult());
    }
}
