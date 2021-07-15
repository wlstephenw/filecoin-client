package com.nenglian.filecoin.rpc.api;

import com.nenglian.filecoin.rpc.domain.MessageSendSpec;
import com.nenglian.filecoin.rpc.domain.types.*;
import com.nenglian.filecoin.rpc.jasonrpc.*;
import java.io.IOException;
import java.math.BigInteger;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author stephen
 */
public class LotusGasAPITest extends LotusChainAPITest {

    private final LotusGasAPI lotusGasAPI = lotusAPIFactory.createLotusGasAPI();

    @Test
    public void estimateFeeCap() throws IOException {
    }

    @Test
    public void estimateGasLimit() {
    }

    @Test
    public void estimateGasPremium() {
    }

    @Test
    public void estimateMessageGas() throws IOException {
        Message message = new com.nenglian.filecoin.rpc.domain.types.Message();
        message.setFrom("f1rfw5ln22fw63llzqyhjrgmx572j5hquyvymmrpq");
        message.setTo("f15baz6uoufdyfodaay4gky4pz6oo5rrpab6yt7pa");
        message.setValue(new BigInteger("100000000000000"));

        MessageSendSpec messageSendSpec = null;

        TipSetKey tsk = null;

        Response<Message> response = lotusGasAPI.estimateMessageGas(message, messageSendSpec, tsk).execute();
        Assert.assertNotNull(response.getResult());
    }
}
