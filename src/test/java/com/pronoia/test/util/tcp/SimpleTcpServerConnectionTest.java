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

import java.net.Socket;
import java.net.SocketException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SimpleTcpServerConnectionTest {
    Logger log = LoggerFactory.getLogger(this.getClass());

    SimpleTcpServer tcpServer;

    @Before
    public void setUp() throws Exception {
        tcpServer = new SimpleTcpServer("test-server").start();
        tcpServer.acceptConnection();
    }

    @After
    public void tearDown() throws Exception {
        tcpServer.stop();
    }

    @Test(timeout = 10000)
    public void testAcceptConnectionByHostName() throws Exception {
        Socket client = new Socket(tcpServer.getInetAddress().getHostName(), tcpServer.getPort());

        Thread.sleep(100);

        assertTrue("Socket should be connected", client.isConnected());
        assertTrue("Client should be connected", tcpServer.isClientConnected());
    }

    @Test(timeout = 10000)
    public void testAcceptConnectionByCanonicalHostName() throws Exception {
        Socket client = new Socket(tcpServer.getInetAddress().getCanonicalHostName(), tcpServer.getPort());

        Thread.sleep(100);

        assertTrue("Socket should be connected", client.isConnected());
        assertTrue("Client should be connected", tcpServer.isClientConnected());
    }


    @Test(timeout = 10000)
    public void testAcceptConnectionByHostAddress() throws Exception {
        Socket client = new Socket(tcpServer.getInetAddress().getHostAddress(), tcpServer.getPort());

        Thread.sleep(100);

        assertTrue("Socket should be connected", client.isConnected());
        assertTrue("Client should be connected", tcpServer.isClientConnected());
    }

    @Test(timeout = 10000)
    public void testCloseConnectionsFollowedByClientAvailable() throws Exception {
        Socket client = new Socket(tcpServer.getInetAddress().getHostAddress(), tcpServer.getPort());

        Thread.sleep(100);

        assertTrue("Socket should be connected", client.isConnected());
        assertTrue("Client should be connected", tcpServer.isClientConnected());

        tcpServer.closeConnections();

        Thread.sleep(100);

        assertEquals(0, client.getInputStream().available());
    }

    @Test(timeout = 10000)
    public void testCloseConnectionsFollowedByClientRead() throws Exception {
        Socket client = new Socket(tcpServer.getInetAddress().getHostAddress(), tcpServer.getPort());

        Thread.sleep(100);

        assertTrue("Socket should be connected", client.isConnected());
        assertTrue("Client should be connected", tcpServer.isClientConnected());

        tcpServer.closeConnections();

        Thread.sleep(100);

        assertEquals(-1, client.getInputStream().read());
    }

    @Test()
    // @Test(timeout = 10000)
    public void testCloseConnectionsFollowedByClientWrite() throws Exception {
        Socket client = new Socket(tcpServer.getInetAddress().getHostAddress(), tcpServer.getPort());

        Thread.sleep(100);

        assertTrue("Socket should be connected", client.isConnected());
        assertTrue("Client should be connected", tcpServer.isClientConnected());

        tcpServer.closeConnections();

        Thread.sleep(100);

        client.getOutputStream().write(1);
    }

    @Test(timeout = 10000)
    public void testResetConnectionsFollowedByClientAvailable() throws Exception {
        Socket client = new Socket(tcpServer.getInetAddress().getHostAddress(), tcpServer.getPort());

        Thread.sleep(100);

        assertTrue("Socket should be connected", client.isConnected());
        assertTrue("Client should be connected", tcpServer.isClientConnected());

        tcpServer.resetConnections();

        Thread.sleep(100);

        assertEquals(0, client.getInputStream().available());
    }

    @Test(timeout = 10000, expected = SocketException.class)
    public void testResetConnectionsFollowedByClientRead() throws Exception {
        Socket client = new Socket(tcpServer.getInetAddress().getHostAddress(), tcpServer.getPort());

        Thread.sleep(100);

        assertTrue("Socket should be connected", client.isConnected());
        assertTrue("Client should be connected", tcpServer.isClientConnected());

        tcpServer.resetConnections();

        Thread.sleep(100);

        assertEquals(-1, client.getInputStream().read());
    }

    @Test(timeout = 10000, expected = SocketException.class)
    public void testResetConnectionsFollowedByClientWrite() throws Exception {
        Socket client = new Socket(tcpServer.getInetAddress().getHostAddress(), tcpServer.getPort());

        Thread.sleep(100);

        assertTrue("Socket should be connected", client.isConnected());
        assertTrue("Client should be connected", tcpServer.isClientConnected());

        tcpServer.resetConnections();

        Thread.sleep(100);

        client.getOutputStream().write(1);
    }

}