package com.pronoia.test.util.tcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

public class SimpleTcpServer {
    Logger log = LoggerFactory.getLogger(this.getClass());

    String name = this.getClass().getSimpleName();

    String host = null;
    int port = 0;
    int backlog = 1;
    int bindTimeout = 60000;
    int acceptTimeout = 60000;
    int receiveTimeout = 15000;
    int readTimeout = 1000;

    private ServerSocket serverSocket = null;

    private List<Socket> connections = new CopyOnWriteArrayList<>();

    public SimpleTcpServer() {
        this(0);
    }

    public SimpleTcpServer(String name) {
        this.name = name;
    }

    public SimpleTcpServer(int port) {
        this.port = port;
    }

    public SimpleTcpServer(String name, int port) {
        this.name = name;
        this.port = port;
    }

    /**
     * Start the TCP Server
     *
     * @return
     */
    public SimpleTcpServer start() {
        if (serverSocket == null) {
            log.trace("{} starting", this.name);
            try {
                serverSocket = new ServerSocket();
            } catch (IOException createEx) {
                throw new RuntimeException(String.format("%s failed to create unbound ServerSocket", name), createEx);
            }

            try {
                serverSocket.setSoTimeout(bindTimeout);
            } catch (SocketException socketEx) {
                throw new RuntimeException(String.format("%s failed to set bind timeout to %d on unbound ServerSocket", name, bindTimeout), socketEx);
            }

            try {
                InetSocketAddress address;
                if (host == null ) {
                    address = new InetSocketAddress(port);
                } else {
                    address = new InetSocketAddress(host, port);
                }
                serverSocket.bind(address, backlog);
                log.info("{} [{}] started", this.name, serverSocket.getLocalSocketAddress().toString());
            } catch (IOException bindEx) {
                throw new RuntimeException(String.format("%s failed to bind ServerSocket to port %d with a backlog of %d", name, port, backlog), bindEx);
            }
        } else {
            log.warn("{} [{}] ignoring attempt to start - server is already running", this.name, serverSocket.getLocalSocketAddress().toString());
        }

        return this;
    }

    /**
     * Stop the TCP Server
     */
    public void stop() {
        if (serverSocket != null) {
            String localSocketAddress = serverSocket.getLocalSocketAddress().toString();
            log.trace("{} [{}] stopping", this.name, localSocketAddress);

            if (connections.size() > 0) {
                this.closeConnections();
            }

            try {
                serverSocket.close();
                log.info("{} [{}] stopped", this.name, localSocketAddress);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            log.warn("{} Ignoring attempt to stop - server is not running", this.name);
        }
    }

    public void acceptConnection() {
        this.acceptConnection(false);
    }

    /**
     * Accept a TCP Connection from a client
     */
    public void acceptConnection(boolean waitForConnection) {
        String serverSocketAddress = serverSocket.getLocalSocketAddress().toString();
        Thread bindThread = new ConnectionAcceptorThread();

        if (waitForConnection) {
            bindThread.run();
        } else {
            bindThread.start();
        }
    }

    /**
     * Close the TCP Connection with the client
     */
    public void closeConnections() {
        String serverSocketAddress = serverSocket.getLocalSocketAddress().toString();

        for (Socket clientSocket : connections) {
            String clientSocketAddress = clientSocket.getRemoteSocketAddress().toString();
            log.trace("{} [{}] closing client connection {}", this.name, serverSocketAddress, clientSocketAddress);
            if (clientSocket.isConnected() && !clientSocket.isClosed()) {
                try {
                    clientSocket.close();
                    log.info("{} [{}] client connection {} closed", this.name, serverSocketAddress, clientSocketAddress);
                } catch (IOException ex) {
                    log.warn(String.format("{} [{}] ignoring exception encountered closing the client connection", this.name, serverSocketAddress, clientSocketAddress), ex);
                }
            }
        }

        connections.clear();
    }

    public int getSoLinger() throws Exception {
        return this.getClientConnection().getSoLinger();
    }

    /**
     * Reset the TCP Connection with the client
     */
    public void resetConnections() {
        String serverSocketAddress = serverSocket.getLocalSocketAddress().toString();

        for (Socket clientSocket : connections) {
            String clientSocketAddress = clientSocket.getRemoteSocketAddress().toString();

            log.trace("{} [{}] resetting client connection {}", this.name, serverSocketAddress, clientSocketAddress);
            if (clientSocket.isConnected() && !clientSocket.isClosed()) {
                final int SO_LINGER_RESET = 0;
                try {
                    clientSocket.setSoLinger(true, SO_LINGER_RESET);
                } catch (SocketException socketEx) {
                    log.warn(String.format("%s [%s] ignoring exception encountered setting SO_LINGER to %d on the socket %s to force a reset", name, serverSocketAddress, SO_LINGER_RESET, clientSocketAddress), socketEx);
                }

                try {
                    clientSocket.close();
                    log.info("{} [{}] client connection {} reset", this.name, serverSocketAddress, clientSocketAddress);
                } catch (IOException ex) {
                    log.warn(String.format("{} [{}] ignoring exception encountered resetting the client connection", this.name, serverSocketAddress, clientSocketAddress), ex);
                }
            }
        }

        connections.clear();
    }

    public boolean isStarted() {
        return (serverSocket != null);
    }

    public boolean isClientConnected() {
        for (Socket clientSocket : connections) {
            if (clientSocket.isConnected() && !clientSocket.isClosed()) {
                return true;
            }
        }
        return false;
    }

    public Socket getClientConnection() {
        if (serverSocket == null) {
            throw new IllegalStateException(String.format("%s cannot get client connection before server has been started", name));
        }

        return connections.get(0);
    }

    public List<Socket> getClientConnections() {
        if (serverSocket == null) {
            throw new IllegalStateException(String.format("%s cannot get list of client connections before server has been started", name));
        }

        return connections;
    }

    public InputStream getInputStream() {
        Socket clientSocket = this.getClientConnection();

        if (clientSocket != null) {
            try {
                return clientSocket.getInputStream();
            } catch (IOException ioEx) {
                throw new RuntimeException(String.format("%s [%s] failed to get InputStream from client socket %s", name, serverSocket.getLocalSocketAddress().toString(), clientSocket.getRemoteSocketAddress().toString()), ioEx);
            }
        } else {
            throw new IllegalStateException(String.format("%s [%s] cannot get input stream before client connection is established", name, serverSocket.getLocalSocketAddress().toString()));
        }
    }

    public OutputStream getOutputStream() {
        Socket clientSocket = this.getClientConnection();

        if (clientSocket != null) {
            try {
                return clientSocket.getOutputStream();
            } catch (IOException ioEx) {
                throw new RuntimeException(String.format("%s [%s] failed to get OutputStream from client socket %s", name, serverSocket.getLocalSocketAddress().toString(), clientSocket.getRemoteSocketAddress().toString()), ioEx);
            }
        } else {
            throw new IllegalStateException(String.format("%s [%s] cannot get output stream before client connection is established", name, serverSocket.getLocalSocketAddress().toString()));
        }
    }

    public int available() {
        int answer = 0;
        Socket connection = getClientConnection();

        if (connection != null) {
            InputStream inputStream = null;
            try {
                inputStream = connection.getInputStream();
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

    // TODO:  Figure out how to do this with a Generic method
    public byte[] read() {
        return doRead().toByteArray();
    }

    public String readString() {
        return doRead().toString();
    }

    public SimpleTcpServer write(byte[] data) {
        OutputStream outputStream = getOutputStream();

        try {
            outputStream.write(data);
        } catch (IOException e) {
            // TODO: Deal with this
            e.printStackTrace();
        }

        return this;
    }

    public SimpleTcpServer write(String dataString) {
        this.write(dataString.getBytes());

        return this;
    }

    public SimpleTcpServer flush() {
        OutputStream outputStream = getOutputStream();

        try {
            outputStream.flush();
        } catch (IOException e) {
            // TODO: Deal with this
            e.printStackTrace();
        }

        return this;
    }

    protected ByteArrayOutputStream doRead() {
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
        return data;
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
        if (serverSocket != null) {
            throw new IllegalAccessError(String.format("%s [%s] cannot set host after server has been started", name, serverSocket.getLocalSocketAddress().toString()));
        }
        this.host = host;
    }

    public int getPort() {
        if (serverSocket != null) {
            return serverSocket.getLocalPort();
        }

        return port;
    }

    public void setPort(int port) {
        final int MAX_PORT_VALUE = 65535;

        if (serverSocket != null) {
            throw new IllegalAccessError(String.format("%s [%s] cannot set port after server has been started", name, serverSocket.getLocalSocketAddress().toString()));
        }

        if (port > MAX_PORT_VALUE) {
            throw new IllegalArgumentException("Port value must be less than or equal to " + MAX_PORT_VALUE);
        }

        this.port = port;
    }

    public InetAddress getInetAddress() {
        if (serverSocket == null) {
            throw new IllegalStateException(String.format("%s cannot get InetAddress before server has been started", name));
        }
        return serverSocket.getInetAddress();
    }

    public SocketAddress getSocketAddress() {
        if (serverSocket == null) {
            throw new IllegalStateException(String.format("%s cannot get SocketAddress before server has been started", name));
        }
        return serverSocket.getLocalSocketAddress();
    }


    public int getBindTimeout() {
        return bindTimeout;
    }

    public void setBindTimeout(int bindTimeout) {
        this.bindTimeout = bindTimeout;
    }

    public void setBindTimeout(int timeout, TimeUnit unit) {
        this.bindTimeout = (int) TimeUnit.MILLISECONDS.convert(timeout, unit);
    }

    public int getAcceptTimeout() {
        return acceptTimeout;
    }

    public void setAcceptTimeout(int acceptTimeout) {
        this.acceptTimeout = acceptTimeout;
    }

    public void setAcceptTimeout(int timeout, TimeUnit unit) {
        this.acceptTimeout = (int) TimeUnit.MILLISECONDS.convert(timeout, unit);
    }

    public int getReceiveTimeout() {
        return receiveTimeout;
    }

    public void setReceiveTimeout(int receiveTimeout) {
        this.receiveTimeout = receiveTimeout;
    }

    public void setReceiveTimeout(int timeout, TimeUnit unit) {
        this.receiveTimeout = (int) TimeUnit.MILLISECONDS.convert(timeout, unit);
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public void setReadTimeout(int timeout, TimeUnit unit) {
        this.readTimeout = (int) TimeUnit.MILLISECONDS.convert(timeout, unit);
    }

    public int getBacklog() {
        return backlog;
    }

    public void setBacklog(int backlog) {
        this.backlog = backlog;
    }

    // Builder-style setters
    public SimpleTcpServer name(String name) {
        this.setName(name);

        return this;
    }

    public SimpleTcpServer port(int port) {
        this.setPort(port);

        return this;
    }

    public SimpleTcpServer bindTimeout(int timeout) {
        this.setBindTimeout(timeout);

        return this;
    }

    public SimpleTcpServer bindTimeout(int timeout, TimeUnit unit) {
        this.setBindTimeout(timeout, unit);

        return this;
    }

    public SimpleTcpServer acceptTimeout(int timeout) {
        this.setAcceptTimeout(timeout);

        return this;
    }

    public SimpleTcpServer acceptTimeout(int timeout, TimeUnit unit) {
        this.setAcceptTimeout(timeout, unit);

        return this;
    }

    public SimpleTcpServer receiveTimeout(int timeout) {
        this.setReceiveTimeout(timeout);

        return this;
    }

    public SimpleTcpServer receiveTimeout(int timeout, TimeUnit unit) {
        this.setReceiveTimeout(timeout, unit);

        return this;
    }

    public SimpleTcpServer readTimeout(int timeout) {
        this.setReadTimeout(timeout);

        return this;
    }

    public SimpleTcpServer readTimeout(int timeout, TimeUnit unit) {
        this.setReadTimeout(timeout, unit);

        return this;
    }

    public SimpleTcpServer backlog(int backlog) {
        this.setBacklog(backlog);

        return this;
    }

    class ConnectionAcceptorThread extends Thread {
        @Override
        public void run() {
            String serverSocketAddress = serverSocket.getLocalSocketAddress().toString();
            try {
                serverSocket.setSoTimeout(acceptTimeout);
            } catch (SocketException socketEx) {
                throw new RuntimeException(String.format("%s failed to set accept timeout to %d on unbound ServerSocket", name, acceptTimeout), socketEx);
            }

            log.trace("{} [{}] ready to accept client connection", name, serverSocketAddress);
            try {
                Socket clientSocket = serverSocket.accept();
                connections.add(clientSocket);
                log.info("{} [{}] accepted client connection {}", name, serverSocketAddress, clientSocket.getRemoteSocketAddress());
            } catch (IOException ioEx) {
                log.warn(String.format("%s [%s] ignoring exception encountered attempting to accept a client connection", name, serverSocketAddress), ioEx);
            }
        }
    }
}
