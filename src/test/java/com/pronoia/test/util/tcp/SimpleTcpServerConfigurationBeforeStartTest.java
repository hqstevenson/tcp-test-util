package com.pronoia.test.util.tcp;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class SimpleTcpServerConfigurationBeforeStartTest {
    static final int TEST_TIMEOUT_MINUTES = 5;
    static final int TEST_TIMEOUT_MILLIS = (int) TimeUnit.MILLISECONDS.convert(TEST_TIMEOUT_MINUTES, TimeUnit.MINUTES);

    SimpleTcpServer tcpServer;

    @Before
    public void setUp() throws Exception {
        tcpServer = new SimpleTcpServer("test-server");
    }

    @After
    public void tearDown() throws Exception {
        if (tcpServer.isStarted()) {
            tcpServer.stop();
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testGetInputStream() throws Exception {
        tcpServer.getInputStream();
    }

    @Test(expected = IllegalStateException.class)
    public void testGetOutputStream() throws Exception {
        tcpServer.getOutputStream();
    }

    @Test
    public void testGetHost() throws Exception {
        final String TEST_HOST = "A Very Bad Hostname for testing";
        tcpServer.host = TEST_HOST;

        assertEquals(TEST_HOST, tcpServer.getHost());
    }

    @Test
    public void testGetPort() throws Exception {
        tcpServer.port = Integer.MAX_VALUE;

        assertEquals(Integer.MAX_VALUE, tcpServer.getPort());
    }

    @Test(expected = IllegalStateException.class)
    public void testGetInetAddress() throws Exception {
        tcpServer.getInetAddress();
    }

    @Test(expected = IllegalStateException.class)
    public void testGetSocketAddress() throws Exception {
        tcpServer.getSocketAddress();
    }

    @Test
    public void testGetBacklog() throws Exception {
        tcpServer.backlog = Integer.MAX_VALUE;

        assertEquals(Integer.MAX_VALUE, tcpServer.getBacklog());
    }

    @Test
    public void testGetBindTimeout() throws Exception {
        tcpServer.bindTimeout = Integer.MAX_VALUE;

        assertEquals(Integer.MAX_VALUE, tcpServer.getBindTimeout());
    }

    @Test
    public void testGetAcceptTimeout() throws Exception {
        tcpServer.acceptTimeout = Integer.MAX_VALUE;

        assertEquals(Integer.MAX_VALUE, tcpServer.getAcceptTimeout());
    }

    @Test
    public void testGetReceiveTimeout() throws Exception {
        tcpServer.receiveTimeout = Integer.MAX_VALUE;

        assertEquals(Integer.MAX_VALUE, tcpServer.getReceiveTimeout());
    }

    @Test
    public void testGetReadTimeout() throws Exception {
        tcpServer.readTimeout = Integer.MAX_VALUE;

        assertEquals(Integer.MAX_VALUE, tcpServer.getReadTimeout());
    }

    @Test
    public void testSetHost() throws Exception {
        final String TEST_HOST_NAME = "A_BAD_HOSTNAME_FOR_TESTING.apache.org";
        tcpServer.setHost(TEST_HOST_NAME);

        assertEquals(TEST_HOST_NAME, tcpServer.host);
    }

    @Test
    public void testSetPort() throws Exception {
        final int TEST_PORT_VALUE = 54321;
            tcpServer.setPort(TEST_PORT_VALUE);

        assertEquals(TEST_PORT_VALUE, tcpServer.port);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetPortWithTooLargeOfAPortNumber() throws Exception {
        tcpServer.setPort(Integer.MAX_VALUE);
    }

    @Test
    public void testSetBacklog() throws Exception {
        tcpServer.setBacklog(Integer.MAX_VALUE);

        assertEquals(Integer.MAX_VALUE, tcpServer.backlog);
    }

    @Test
    public void testSetBindTimeout() throws Exception {
        tcpServer.bindTimeout = -1;

        // Test standard setter
        tcpServer.setBindTimeout(TEST_TIMEOUT_MILLIS);

        assertEquals(TEST_TIMEOUT_MILLIS, tcpServer.bindTimeout);

        tcpServer.bindTimeout = -1;

        // Test setter with TimeUnit
        tcpServer.setBindTimeout(5, TimeUnit.MINUTES);

        assertEquals(TEST_TIMEOUT_MILLIS, tcpServer.bindTimeout);

        tcpServer.bindTimeout = -1;

        // Test builder-style setter
        assertSame(tcpServer, tcpServer.bindTimeout(TEST_TIMEOUT_MILLIS));
        assertEquals(TEST_TIMEOUT_MILLIS, tcpServer.bindTimeout);

        tcpServer.bindTimeout = -1;

        // Test builder-style setter with TimeUnit
        assertSame(tcpServer, tcpServer.bindTimeout(TEST_TIMEOUT_MINUTES, TimeUnit.MINUTES));
        assertEquals(TEST_TIMEOUT_MILLIS, tcpServer.bindTimeout);
    }

    @Test
    public void testSetAcceptTimeout() throws Exception {
        tcpServer.acceptTimeout = -1;

        // Test standard setter
        tcpServer.setAcceptTimeout(TEST_TIMEOUT_MILLIS);

        assertEquals(TEST_TIMEOUT_MILLIS, tcpServer.acceptTimeout);

        tcpServer.acceptTimeout = -1;

        // Test setter with TimeUnit
        tcpServer.setAcceptTimeout(5, TimeUnit.MINUTES);

        assertEquals(TEST_TIMEOUT_MILLIS, tcpServer.acceptTimeout);

        tcpServer.acceptTimeout = -1;

        // Test builder-style setter
        assertSame(tcpServer, tcpServer.acceptTimeout(TEST_TIMEOUT_MILLIS));
        assertEquals(TEST_TIMEOUT_MILLIS, tcpServer.acceptTimeout);

        tcpServer.acceptTimeout = -1;

        // Test builder-style setter with TimeUnit
        assertSame(tcpServer, tcpServer.acceptTimeout(TEST_TIMEOUT_MINUTES, TimeUnit.MINUTES));
        assertEquals(TEST_TIMEOUT_MILLIS, tcpServer.acceptTimeout);
    }

    @Test
    public void testSetReceiveTimeout() throws Exception {
        tcpServer.receiveTimeout = -1;

        // Test standard setter
        tcpServer.setReceiveTimeout(TEST_TIMEOUT_MILLIS);

        assertEquals(TEST_TIMEOUT_MILLIS, tcpServer.receiveTimeout);

        tcpServer.receiveTimeout = -1;

        // Test setter with TimeUnit
        tcpServer.setReceiveTimeout(5, TimeUnit.MINUTES);

        assertEquals(TEST_TIMEOUT_MILLIS, tcpServer.receiveTimeout);

        tcpServer.receiveTimeout = -1;

        // Test builder-style setter
        assertSame(tcpServer, tcpServer.receiveTimeout(TEST_TIMEOUT_MILLIS));
        assertEquals(TEST_TIMEOUT_MILLIS, tcpServer.receiveTimeout);

        tcpServer.receiveTimeout = -1;

        // Test builder-style setter with TimeUnit
        assertSame(tcpServer, tcpServer.receiveTimeout(TEST_TIMEOUT_MINUTES, TimeUnit.MINUTES));
        assertEquals(TEST_TIMEOUT_MILLIS, tcpServer.receiveTimeout);
    }

    @Test
    public void testSetReadTimeout() throws Exception {
        tcpServer.readTimeout = -1;

        // Test standard setter
        tcpServer.setReadTimeout(TEST_TIMEOUT_MILLIS);

        assertEquals(TEST_TIMEOUT_MILLIS, tcpServer.readTimeout);

        tcpServer.readTimeout = -1;

        // Test setter with TimeUnit
        tcpServer.setReadTimeout(5, TimeUnit.MINUTES);

        assertEquals(TEST_TIMEOUT_MILLIS, tcpServer.readTimeout);

        tcpServer.readTimeout = -1;

        // Test builder-style setter
        assertSame(tcpServer, tcpServer.readTimeout(TEST_TIMEOUT_MILLIS));
        assertEquals(TEST_TIMEOUT_MILLIS, tcpServer.readTimeout);

        tcpServer.readTimeout = -1;

        // Test builder-style setter with TimeUnit
        assertSame(tcpServer, tcpServer.readTimeout(TEST_TIMEOUT_MINUTES, TimeUnit.MINUTES));
        assertEquals(TEST_TIMEOUT_MILLIS, tcpServer.readTimeout);
    }

    // TODO:  Add test cases for started server, etc
}