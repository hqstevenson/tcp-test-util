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

import java.net.ServerSocket;
import java.net.Socket;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SimpleTcpClientConnectionTest {
    Logger log = LoggerFactory.getLogger(this.getClass());

    ServerSocket serverSocket;
    Socket connection;
    SimpleTcpClient tcpClient;

    @Before
    public void setUp() throws Exception {
        serverSocket = new ServerSocket(0);
        tcpClient = new SimpleTcpClient("test-client").port(serverSocket.getLocalPort()).start();

        connection = serverSocket.accept();

        assertTrue("Client should be connected", tcpClient.isConnected());
        assertTrue("ServerSocket should be connected", connection.isConnected());
    }

    @After
    public void tearDown() throws Exception {
        if (tcpClient.isStarted()) {
            tcpClient.stop();
        }
    }

    @Test(timeout = 10000)
    public void testIsConnected() throws Exception {
        assertTrue("isConnected should return true before socket is closed", tcpClient.isConnected());

        tcpClient.socket.close();

        assertFalse("isConnected should return false after socket is closed", tcpClient.isConnected());
    }

    @Test(timeout = 10000)
    public void testIsClosed() throws Exception {
        assertFalse("isClosed should return false before socket is closed", tcpClient.isClosed());

        tcpClient.socket.close();

        assertTrue("isClosed should return true after socket is closed", tcpClient.isClosed());
    }

    /*
    @Test(timeout = 10000)
    public void testCloseConnectionsFollowedByClientAvailable() throws Exception {
        Socket client = new Socket(tcpServer.getInetAddress().getHostAddress(), tcpServer.getPort());

        Thread.sleep(100);

        assertTrue("Socket should be connected", client.isConnected());
        assertTrue("Client should be connected", tcpServer.isClientConnected());

        tcpServer.closeConnections();

        Thread.sleep(100);

        assertEquals( 0, client.getInputStream().available());
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

    @Test(timeout = 10000)
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

        assertEquals( 0, client.getInputStream().available());
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
    */
}