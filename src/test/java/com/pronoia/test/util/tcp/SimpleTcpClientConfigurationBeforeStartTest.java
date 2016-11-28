package com.pronoia.test.util.tcp;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class SimpleTcpClientConfigurationBeforeStartTest {
    static final int TEST_TIMEOUT_MINUTES = 5;
    static final int TEST_TIMEOUT_MILLIS = (int) TimeUnit.MILLISECONDS.convert(TEST_TIMEOUT_MINUTES, TimeUnit.MINUTES);

    SimpleTcpClient tcpClient;

    @Before
    public void setUp() throws Exception {
        tcpClient = new SimpleTcpClient("test-client");
    }

    @After
    public void tearDown() throws Exception {
        if (tcpClient.isStarted()) {
            tcpClient.stop();
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testGetInputStream() throws Exception {
        tcpClient.getInputStream();
    }

    @Test(expected = IllegalStateException.class)
    public void testGetOutputStream() throws Exception {
        tcpClient.getOutputStream();
    }

    @Test
    public void testGetHost() throws Exception {
        final String TEST_HOST = "A Very Bad Hostname for testing";
        tcpClient.host = TEST_HOST;

        assertEquals(TEST_HOST, tcpClient.getHost());
    }

    @Test
    public void testGetPort() throws Exception {
        tcpClient.port = Integer.MAX_VALUE;

        assertEquals(Integer.MAX_VALUE, tcpClient.getPort());
    }

    @Test(expected = IllegalStateException.class)
    public void testGetInetAddress() throws Exception {
        tcpClient.getInetAddress();
    }

    @Test(expected = IllegalStateException.class)
    public void testGetLocalSocketAddress() throws Exception {
        tcpClient.getLocalSocketAddress();
    }

    @Test(expected = IllegalStateException.class)
    public void testGetRemoteSocketAddress() throws Exception {
        tcpClient.getRemoteSocketAddress();
    }

    @Test
    public void testGetConnectTimeout() throws Exception {
        tcpClient.connectTimeout = Integer.MAX_VALUE;

        assertEquals(Integer.MAX_VALUE, tcpClient.getConnectTimeout());
    }

    @Test
    public void testGetReceiveTimeout() throws Exception {
        tcpClient.receiveTimeout = Integer.MAX_VALUE;

        assertEquals(Integer.MAX_VALUE, tcpClient.getReceiveTimeout());
    }

    @Test
    public void testGetReadTimeout() throws Exception {
        tcpClient.readTimeout = Integer.MAX_VALUE;

        assertEquals(Integer.MAX_VALUE, tcpClient.getReadTimeout());
    }

    @Test
    public void testSetHost() throws Exception {
        final String TEST_HOST_NAME = "A_BAD_HOSTNAME_FOR_TESTING.apache.org";
        tcpClient.setHost(TEST_HOST_NAME);

        assertEquals(TEST_HOST_NAME, tcpClient.host);
    }

    @Test
    public void testSetPort() throws Exception {
        final int TEST_PORT_VALUE = 54321;
            tcpClient.setPort(TEST_PORT_VALUE);

        assertEquals(TEST_PORT_VALUE, tcpClient.port);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetPortWithTooLargeOfAPortNumber() throws Exception {
        tcpClient.setPort(Integer.MAX_VALUE);
    }

    @Test
    public void testSetConnectTimeout() throws Exception {
        tcpClient.connectTimeout = -1;

        // Test standard setter
        tcpClient.setConnectTimeout(TEST_TIMEOUT_MILLIS);

        assertEquals(TEST_TIMEOUT_MILLIS, tcpClient.connectTimeout);

        tcpClient.connectTimeout = -1;

        // Test setter with TimeUnit
        tcpClient.setConnectTimeout(5, TimeUnit.MINUTES);

        assertEquals(TEST_TIMEOUT_MILLIS, tcpClient.connectTimeout);

        tcpClient.connectTimeout = -1;

        // Test builder-style setter
        assertSame(tcpClient, tcpClient.connectTimeout(TEST_TIMEOUT_MILLIS));
        assertEquals(TEST_TIMEOUT_MILLIS, tcpClient.connectTimeout);

        tcpClient.connectTimeout = -1;

        // Test builder-style setter with TimeUnit
        assertSame(tcpClient, tcpClient.connectTimeout(TEST_TIMEOUT_MINUTES, TimeUnit.MINUTES));
        assertEquals(TEST_TIMEOUT_MILLIS, tcpClient.connectTimeout);
    }

    @Test
    public void testSetReceiveTimeout() throws Exception {
        tcpClient.receiveTimeout = -1;

        // Test standard setter
        tcpClient.setReceiveTimeout(TEST_TIMEOUT_MILLIS);

        assertEquals(TEST_TIMEOUT_MILLIS, tcpClient.receiveTimeout);

        tcpClient.receiveTimeout = -1;

        // Test setter with TimeUnit
        tcpClient.setReceiveTimeout(5, TimeUnit.MINUTES);

        assertEquals(TEST_TIMEOUT_MILLIS, tcpClient.receiveTimeout);

        tcpClient.receiveTimeout = -1;

        // Test builder-style setter
        assertSame(tcpClient, tcpClient.receiveTimeout(TEST_TIMEOUT_MILLIS));
        assertEquals(TEST_TIMEOUT_MILLIS, tcpClient.receiveTimeout);

        tcpClient.receiveTimeout = -1;

        // Test builder-style setter with TimeUnit
        assertSame(tcpClient, tcpClient.receiveTimeout(TEST_TIMEOUT_MINUTES, TimeUnit.MINUTES));
        assertEquals(TEST_TIMEOUT_MILLIS, tcpClient.receiveTimeout);
    }

    @Test
    public void testSetReadTimeout() throws Exception {
        tcpClient.readTimeout = -1;

        // Test standard setter
        tcpClient.setReadTimeout(TEST_TIMEOUT_MILLIS);

        assertEquals(TEST_TIMEOUT_MILLIS, tcpClient.readTimeout);

        tcpClient.readTimeout = -1;

        // Test setter with TimeUnit
        tcpClient.setReadTimeout(5, TimeUnit.MINUTES);

        assertEquals(TEST_TIMEOUT_MILLIS, tcpClient.readTimeout);

        tcpClient.readTimeout = -1;

        // Test builder-style setter
        assertSame(tcpClient, tcpClient.readTimeout(TEST_TIMEOUT_MILLIS));
        assertEquals(TEST_TIMEOUT_MILLIS, tcpClient.readTimeout);

        tcpClient.readTimeout = -1;

        // Test builder-style setter with TimeUnit
        assertSame(tcpClient, tcpClient.readTimeout(TEST_TIMEOUT_MINUTES, TimeUnit.MINUTES));
        assertEquals(TEST_TIMEOUT_MILLIS, tcpClient.readTimeout);
    }

    // TODO:  Add test cases for started server, etc
}