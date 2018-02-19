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
package com.pronoia.test.util.tcp;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.Socket;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class SimpleTcpServerReadWriteTest {
    static final String TEST_PAYLOAD = "TEST PAYLOAD";

    Logger log = LoggerFactory.getLogger(this.getClass());

    Socket clientSocket;
    SimpleTcpServer tcpServer;

    @Before
    public void setUp() throws Exception {
        tcpServer = new SimpleTcpServer("test-server").start();
        tcpServer.acceptConnection();

        clientSocket = new Socket("0.0.0.0", tcpServer.getPort());

        Thread.sleep(100);

        log.info("clientSocket - connected to {} - {}", clientSocket.getLocalSocketAddress().toString(), clientSocket.getRemoteSocketAddress().toString());
    }

    @After
    public void tearDown() throws Exception {
        assertEquals("Client socket should not have any available data", 0, clientSocket.getInputStream().available());
        assertEquals("SimpleTcpServer should not have any available data", 0, tcpServer.available());

        clientSocket.close();
        tcpServer.stop();
    }

    @Test
    public void testRead() throws Exception {
        byte[] expected = TEST_PAYLOAD.getBytes();

        clientSocket.getOutputStream().write(expected);

        byte[] actual = tcpServer.read();

        assertArrayEquals("Sent payload should match received payload", expected, actual);
    }

    @Test
    public void testReadWithTwoWrites() throws Exception {
        byte[] payload = TEST_PAYLOAD.getBytes();

        ByteArrayOutputStream expected = new ByteArrayOutputStream(payload.length * 2);
        expected.write(payload);
        expected.write(payload);

        OutputStream socketOutputStream = clientSocket.getOutputStream();
        socketOutputStream.write(payload);
        socketOutputStream.flush();
        socketOutputStream.write(payload);
        socketOutputStream.flush();

        byte[] actual = tcpServer.read();

        assertArrayEquals("Sent payload should match received payload", expected.toByteArray(), actual);
    }

    @Test
    public void testWrite() throws Exception {
        byte[] payload = TEST_PAYLOAD.getBytes();

        tcpServer.write(payload);

        byte[] buffer = new byte[payload.length * 2];
        InputStream socketInputStream = clientSocket.getInputStream();
        int actualCount = socketInputStream.read(buffer);
        ByteArrayOutputStream actual = new ByteArrayOutputStream(buffer.length);
        actual.write(buffer, 0, actualCount);

        assertEquals("Socket should not have any available data", 0, socketInputStream.available());
        assertEquals("Read size does not match expected size", payload.length, actualCount);
        assertArrayEquals("Read payload does not match expected", payload, actual.toByteArray());
    }

    @Test
    public void testWriteChaining() throws Exception {
        byte[] payload = TEST_PAYLOAD.getBytes();

        tcpServer.write(payload).write(" AND ").write(payload);

        ByteArrayOutputStream expected = new ByteArrayOutputStream(payload.length * 2 + 5);
        expected.write(payload);
        expected.write(" AND ".getBytes());
        expected.write(payload);

        Thread.sleep(100);

        byte[] buffer = new byte[expected.size() * 2];
        InputStream socketInputStream = clientSocket.getInputStream();

        int actualCount = socketInputStream.read(buffer);
        ByteArrayOutputStream actual = new ByteArrayOutputStream(buffer.length);
        actual.write(buffer, 0, actualCount);

        assertEquals("Socket should not have any available data", 0, socketInputStream.available());
        assertEquals("Read size does not match expected size", expected.size(), actualCount);
        assertArrayEquals("Read payload does not match expected", expected.toByteArray(), actual.toByteArray());
    }
}