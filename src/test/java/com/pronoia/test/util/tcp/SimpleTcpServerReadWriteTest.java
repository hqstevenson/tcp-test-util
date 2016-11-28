package com.pronoia.test.util.tcp;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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