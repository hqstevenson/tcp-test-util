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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import java.net.ServerSocket;
import java.net.Socket;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SimpleTcpClientReadWriteTest {
    static final String TEST_PAYLOAD = "TEST PAYLOAD";

    Logger log = LoggerFactory.getLogger(this.getClass());

    ServerSocket listener;
    Socket testConnection;
    SimpleTcpClient tcpClient;

    @Before
    public void setUp() throws Exception {
        listener = new ServerSocket(0);
        tcpClient = new SimpleTcpClient("test-client").port(listener.getLocalPort()).start();
        testConnection = listener.accept();

        assertNotNull("Test connection should have been established", testConnection);
    }

    @After
    public void tearDown() throws Exception {
        assertEquals("TcpClient should not have any available data", 0, tcpClient.available());
        assertEquals("Test connection should not have any available data", 0, testConnection.getInputStream().available());

        tcpClient.stop();
        listener.close();
    }

    @Test
    public void testRead() throws Exception {
        byte[] expected = TEST_PAYLOAD.getBytes();

        write(expected);

        byte[] actual = tcpClient.read();

        assertArrayEquals("Actual payload does not match expected", expected, actual);
    }

    @Test
    public void testWrite() throws Exception {
        byte[] expected = TEST_PAYLOAD.getBytes();

        tcpClient.write(expected);

        byte[] actual = read();

        assertArrayEquals("Actual payload does not match expected", expected, actual);
    }

    @Test
    public void testWriteChaining() throws Exception {
        byte[] payload = TEST_PAYLOAD.getBytes();

        ByteArrayOutputStream expected = new ByteArrayOutputStream(payload.length * 2);
        expected.write(payload);
        expected.write(payload);

        tcpClient.write(payload).flush().write(payload);

        byte[] actual = read();

        assertArrayEquals("Actual payload does not match expected", expected.toByteArray(), actual);
    }

    private byte[] read() throws Exception {
        ByteArrayOutputStream received = new ByteArrayOutputStream(TEST_PAYLOAD.length());

        InputStream inputStream = testConnection.getInputStream();
        while (inputStream.available() > 0) {
            received.write(inputStream.read());
        }

        return received.toByteArray();
    }

    private void write(byte[] payload) throws Exception {
        testConnection.getOutputStream().write(payload);
    }

    private void flush() throws Exception {
        testConnection.getOutputStream().flush();
    }

}