package com.nenglian.filecoin.transaction;

import static org.junit.Assert.*;

import java.io.IOException;
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
}
