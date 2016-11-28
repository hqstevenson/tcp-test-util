package com.pronoia.test.util.tcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.concurrent.TimeUnit;

public class SimpleTcpClient {
    Logger log = LoggerFactory.getLogger(this.getClass());

    Socket socket;

    String name = this.getClass().getSimpleName();
    String host;
    int port = -1;

    int connectTimeout = 15000;
    int receiveTimeout = 15000;
    int readTimeout = 1000;

    public SimpleTcpClient() {
    }

    public SimpleTcpClient(String name) {
        this.name = name;
    }

    public SimpleTcpClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public SimpleTcpClient(String name, String host, int port) {
        this.name = name;
        this.host = host;
        this.port = port;
    }

    public SimpleTcpClient start() {
        if (socket == null) {
            connect();
        } else {
            log.warn("{} ignoring start attempt on a started client: {} -> {}", name, socket.getLocalSocketAddress().toString(), socket.getRemoteSocketAddress().toString());
        }

        return this;
    }

    public void connect() {
        if (socket != null) {
            throw new IllegalStateException(String.format("Cannot connect when already connected: %s -> %s", socket.getLocalSocketAddress().toString(), socket.getRemoteSocketAddress().toString()));
        }

        InetSocketAddress address;
        if (host != null) {
            address = new InetSocketAddress(host, port);
        } else {
            address = new InetSocketAddress(port);
        }

        try {
            Socket tmpSocket = new Socket();
            tmpSocket.connect(address, connectTimeout);
            socket = tmpSocket;
        } catch (SocketTimeoutException timeoutEx) {
            throw new RuntimeException(String.format("SocketTimeoutException encountered after %d-ms when attempting to connecting to address %s", connectTimeout, address.toString()));
        } catch (IOException e) {
            throw new RuntimeException("Unexpected Exception encountered connecting to address: " + address);
        }

    }

    public void stop() {
        if (socket == null) {
            log.warn("{} ignoring stop attempt on a stopped client", name);
        } else {
            close();
        }

    }

    public void close() {
        if (socket == null) {
            throw new IllegalStateException(String.format("%s cannot close when client has not been started", name));
        }

        SocketAddress localSocketAddress = socket.getLocalSocketAddress();
        SocketAddress remoteSocketAddress = socket.getRemoteSocketAddress();
        try {
            socket.close();
        } catch (IOException e) {
            log.warn("{} ignoring exception encounter when attempting to close connection: {} -> {}", name, localSocketAddress.toString(), remoteSocketAddress.toString());
        } finally {
            socket = null;
        }
    }

    public void reset() {
        if (socket == null) {
            throw new IllegalStateException(String.format("%s cannot reset when client has not been started", name));
        }

        final int SO_LINGER_RESET = 0;

        SocketAddress localSocketAddress = socket.getLocalSocketAddress();
        SocketAddress remoteSocketAddress = socket.getRemoteSocketAddress();

        try {
            socket.setSoLinger(true, SO_LINGER_RESET);
        } catch (SocketException socketEx) {
            log.warn(String.format("%s ignoring SocketException encounter when attempting to close connection: %s -> %s", name, localSocketAddress.toString(), remoteSocketAddress.toString()), socketEx);
        }
        try {
            socket.close();
        } catch (IOException ioEx) {
            log.warn(String.format("%s ignoring IOException encounter when attempting to close connection: %s -> %s", name, localSocketAddress.toString(), remoteSocketAddress.toString()), ioEx);
        } finally {
            socket = null;
        }
    }

    public boolean isStarted() {
        return (socket != null);
    }

    public boolean isConnected() {
        return (isStarted() && socket.isConnected() && !socket.isClosed());
    }

    public boolean isClosed() {
        return (isStarted() && socket.isConnected() && socket.isClosed());
    }

    public InputStream getInputStream() {
        if (socket == null) {
            throw new IllegalStateException(String.format("%s cannot get socket input stream when client has not been started", name));
        }

        try {
            return socket.getInputStream();
        } catch (IOException ioEx) {
            throw new IllegalStateException(String.format("%s failed to get InputStream from client socket %s -> %s", name, socket.getLocalSocketAddress().toString(), socket.getRemoteSocketAddress().toString()), ioEx);
        }
    }

    public OutputStream getOutputStream() {
        if (socket == null) {
            throw new IllegalStateException(String.format("%s cannot get socket input stream when client has not been started", name));
        }

        try {
            return socket.getOutputStream();
        } catch (IOException ioEx) {
            throw new IllegalStateException(String.format("%s failed to get OutputStream from client socket %s -> %s", name, socket.getLocalSocketAddress().toString(), socket.getRemoteSocketAddress().toString()), ioEx);
        }
    }

    public int available() {
        int answer = 0;
        if (socket != null) {
            InputStream inputStream = null;
            try {
                inputStream = socket.getInputStream();
            } catch (IOException getStreamEx) {
                log.warn("{} ignoring exception encountered when retrieving the input stream to determine data availability", getStreamEx);
            }
            if (inputStream != null) {
                try {
                    answer = inputStream.available();
                } catch (IOException availableEx) {
                    log.warn("{} ignoring exception encountered when determining if data is available from the input stream", availableEx);
                }
            }
        }

        return answer;
    }

    public byte[] read() {
        if (socket == null) {
            throw new IllegalStateException(String.format("%s cannot read before client has been started", name));
        }

        InputStream inputStream = getInputStream();

        ByteArrayOutputStream data = new ByteArrayOutputStream(1024);
        try {
            while (inputStream.available() > 0) {
                byte[] buffer = new byte[1024];
                int readCount = 0;
                readCount = inputStream.read(buffer);
                data.write(buffer, 0, readCount);
            }
        } catch (IOException e) {
            // TODO: Deal with this
            e.printStackTrace();
        }
        return data.toByteArray();
    }

    public SimpleTcpClient write(byte[] data) {
        if (socket == null) {
            throw new IllegalStateException(String.format("%s cannot write before client has been started", name));
        }

        OutputStream outputStream = getOutputStream();

        try {
            outputStream.write(data);
        } catch (IOException e) {
            // TODO: Deal with this
            e.printStackTrace();
        }

        return this;
    }

    public SimpleTcpClient flush() {
        if (socket == null) {
            throw new IllegalStateException(String.format("%s cannot flush output stream before client has been started", name));
        }

        OutputStream outputStream = getOutputStream();

        try {
            outputStream.flush();
        } catch (IOException e) {
            // TODO: Deal with this
            e.printStackTrace();
        }

        return this;
    }


    // Getters & Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        if (socket != null) {
            throw new IllegalStateException(String.format("%s [%s:%d] cannot set port while client is running", name, host, port));
        }

        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        if (socket != null) {
            throw new IllegalStateException(String.format("%s [%s:%d] cannot set port while client is running", name, host, port));
        }

        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException(String.format("%s - illegal port value [%d].  Port must be between 1 and 65535", name, port));
        }

        this.port = port;
    }

    public InetAddress getInetAddress() {
        if (socket == null) {
            throw new IllegalStateException(String.format("%s cannot get INET address before client is connected", name));
        }

        return socket.getInetAddress();
    }

    public SocketAddress getLocalSocketAddress() {
        if (socket == null) {
            throw new IllegalStateException(String.format("%s cannot get local socket address before client is connected", name));
        }

        return socket.getLocalSocketAddress();
    }

    public SocketAddress getRemoteSocketAddress() {
        if (socket == null) {
            throw new IllegalStateException(String.format("%s cannot get remote socket address before client is connected", name));
        }

        return socket.getRemoteSocketAddress();
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int timeout) {
        this.connectTimeout = timeout;
    }

    public void setConnectTimeout(int timeout, TimeUnit unit) {
        this.connectTimeout = (int) TimeUnit.MILLISECONDS.convert(timeout, unit);
    }

    public int getReceiveTimeout() {
        return receiveTimeout;
    }

    public void setReceiveTimeout(int timeout) {
        this.receiveTimeout = timeout;
    }

    public void setReceiveTimeout(int timeout, TimeUnit unit) {
        this.receiveTimeout = (int) TimeUnit.MILLISECONDS.convert(timeout, unit);
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int timeout) {
        this.readTimeout = timeout;
    }

    public void setReadTimeout(int timeout, TimeUnit unit) {
        this.readTimeout = (int) TimeUnit.MILLISECONDS.convert(timeout, unit);
    }

    // Builder-style setters
    public SimpleTcpClient name(String name) {
        this.setName(name);

        return this;
    }

    public SimpleTcpClient host(String host) {
        this.setHost(host);

        return this;
    }

    public SimpleTcpClient port(int port) {
        this.setPort(port);

        return this;
    }

    public SimpleTcpClient connectTimeout(int timeout) {
        this.setConnectTimeout(timeout);

        return this;
    }

    public SimpleTcpClient connectTimeout(int timeout, TimeUnit unit) {
        this.setConnectTimeout(timeout, unit);

        return this;
    }

    public SimpleTcpClient receiveTimeout(int timeout) {
        this.setReceiveTimeout(timeout);

        return this;
    }

    public SimpleTcpClient receiveTimeout(int timeout, TimeUnit unit) {
        this.setReceiveTimeout(timeout, unit);

        return this;
    }

    public SimpleTcpClient readTimeout(int timeout) {
        this.setReadTimeout(timeout);

        return this;
    }

    public SimpleTcpClient readTimeout(int timeout, TimeUnit unit) {
        this.setReadTimeout(timeout, unit);

        return this;
    }
}
