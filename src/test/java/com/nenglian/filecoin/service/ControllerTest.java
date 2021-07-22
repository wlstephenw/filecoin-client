package com.nenglian.filecoin.service;

import static org.junit.Assert.*;

import com.nenglian.filecoin.service.api.Result;
import com.nenglian.filecoin.service.api.Transfer;
import com.nenglian.filecoin.transaction.TransactionListener;
import com.nenglian.filecoin.transaction.TransactionManager;
import java.io.IOException;
import java.math.BigInteger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * @author stephen
 * @since 2021/7/21 下午6:47
 */

@SpringBootTest
@RunWith(SpringRunner.class)
public class ControllerTest {

    @Autowired
    TransactionListener listener;

    @Autowired
    Controller controller;

    @Test
    public void transfer() throws InterruptedException, IOException {

        Transfer transfer = Transfer.builder()
            .from("f1f6giluhaka4myeah5hq4w4e6vt64pzwa74lsrsi")
            .to("f1oguiqe7dry7orwvriipthlwo57ojbr4tbxrexvq")
            .value(BigInteger.ONE)
            .gasAddress("f3upax7iznvckntkata6srcggnysr4vfltmh7aurxyhxmpahqpaxa6sjzy3uziyefjkb44lsozdp2snokloxqq")
            .build();

        System.out.println("head is: " + listener.headHeight());
        controller.setPollHeight(listener.headHeight() - 1);
        Result<String> result = controller.transfer(transfer);

        Assert.assertTrue(result != null);
        Thread.sleep(100 * 1000);
    }
}
