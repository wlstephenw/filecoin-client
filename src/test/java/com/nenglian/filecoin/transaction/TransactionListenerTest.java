package com.nenglian.filecoin.transaction;

import com.nenglian.filecoin.rpc.domain.cid.Cid;
import com.nenglian.filecoin.rpc.domain.types.Message;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;


/**
 * @author stephen
 * @since 2021/7/15 上午11:56
 */

public class TransactionListenerTest {
    TransactionListener listener = new TransactionListener();

    @Before
    public void setUp() {
        final Logger logger = (Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        logger.setLevel(Level.INFO);
    }

    @Test
    public void listen() throws IOException, InterruptedException {
        new TransactionListener().listen();
        Thread.sleep(100 * 1000);
    }

    @Test
    public void getMessagesFutureByHeightRange()
        throws IOException, ExecutionException, InterruptedException, ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Long head = listener.headHeight();
//        Date from = new Date(listener.getBlockHeader(head - 100).getTimestamp());
//        Date to = new Date(listener.getBlockHeader(head - 1).getTimestamp());

        Date from = format.parse("1970-01-15 23:52:51");
        Date to = format.parse("1971-01-15 23:52:49");

        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(from));
        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(to));


        CompletableFuture<Map<Cid, Message>> future = listener
            .getMessagesFutureByHeightRange(from, to);

        Map<Cid, Message> map = future.get();

        Assert.assertNotNull(map);
    }
}
