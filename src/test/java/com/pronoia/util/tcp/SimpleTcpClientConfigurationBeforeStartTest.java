/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pronoia.util.tcp;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
        final String testHost = "A Very Bad Hostname for testing";
        tcpClient.host = testHost;

        assertEquals(testHost, tcpClient.getHost());
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
        final String testHostName = "A_BAD_HOSTNAME_FOR_TESTING.apache.org";
        tcpClient.setHost(testHostName);

        assertEquals(testHostName, tcpClient.host);
    }

    @Test
    public void testSetPort() throws Exception {
        final int testPortValue = 54321;
        tcpClient.setPort(testPortValue);

        assertEquals(testPortValue, tcpClient.port);
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