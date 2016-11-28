package com.pronoia.test.util.tcp;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.net.*;

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

}