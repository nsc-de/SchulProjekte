package schule.projects.e2e_chat;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class TCPServer extends Thread {

    private final int port;
    private ServerSocket serverSocket;
    private ClientManager connectionManager;
    private boolean running = false;
    private final TCPServerHandler handler;

    public TCPServer(int port, TCPServerHandler handler) {
        this.port = port;
        this.handler = handler;
    }

    @Override
    public void run() {
        running = true;
        connectionManager = new ClientManager();
        connectionManager.start();
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            this.serverSocket = serverSocket;
            while (running) {
                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket);
                connectionManager.addClient(clientHandler);
                handler.handleConnection(new TCPServerHandler.TCPConnectionEvent(getServer(), clientHandler));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() throws IOException {
        handler.handleServerClose(new TCPServerHandler.TCPServerCloseEvent(getServer()));
        running = false;
        connectionManager.close();
        serverSocket.close();
    }

    public TCPServer getServer() {
        return this;
    }

    private class ClientManager extends Thread {

        private final List<ClientHandler> clientHandlers = new ArrayList<>();
        private boolean running;

        public synchronized void addClient(ClientHandler clientHandler) {
            clientHandlers.add(clientHandler);
        }

        @Override
        public void run() {
            this.running = true;
            try {
                while(running) {
                    tick();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public synchronized void tick() throws IOException {
            for(int i = 0; i < clientHandlers.size(); i++) {
                ClientHandler clientHandler = clientHandlers.get(i);
                if (clientHandler.isClosed()) {
                    if(clientHandler.closeHandled()) {
                        handler.handleDisconnect(
                                new TCPServerHandler.TCPClientConnectionDisconnectEvent(
                                        getServer(),
                                        clientHandler
                                ));
                    }
                    clientHandlers.remove(i--);
                }
                else clientHandler.tick();
            }
        }

        public void close() {
            running = false;
        }

        public List<ClientHandler> getClientHandlers() {
            return clientHandlers;
        }
    }

    public class ClientHandler {
        private final Socket client;
        private boolean closed;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public ClientHandler(Socket client) throws IOException {
            this.client = client;
            this.inputStream = new BufferedInputStream(client.getInputStream());
            this.outputStream = client.getOutputStream();
        }

        public synchronized void tick() throws IOException {
            if(inputStream.available() > 0) {

                byte[] buffer = new byte[inputStream.available()];
                if(inputStream.read(buffer) != buffer.length) throw new IOException("Could not read all bytes");

                handler.handleMessage(new TCPServerHandler.TCPClientMessageEvent(getServer(), getClient(), buffer));

            }
        }

        public void send(byte[] message) throws IOException {
            outputStream.write(message);
        }

        public void send(String message) throws IOException {
            send(message.getBytes());
        }

        public void close() throws IOException {
            this.closed = true;
            handler.handleClientClose(new TCPServerHandler.TCPClientConnectionCloseEvent(getServer(), getClient()));
            client.close();
        }

        public ClientHandler getClient() {
            return this;
        }

        public Socket getSocket() {
            return client;
        }
        public boolean closeHandled() {
            return closed;
        }
        public boolean isClosed() {
            return client.isClosed();
        }
    }



    interface TCPServerHandler {

        default void handleConnection(TCPConnectionEvent event) {}
        default void handleMessage(TCPClientMessageEvent event) {}
        default void handleDisconnect(TCPClientConnectionDisconnectEvent event) {}
        default void handleClientClose(TCPClientConnectionCloseEvent event) {}
        default void handleServerClose(TCPServerCloseEvent event) {}

        class TCPConnectionEvent {

            private final TCPServer server;
            private final TCPServer.ClientHandler client;

            public TCPConnectionEvent(TCPServer server, TCPServer.ClientHandler clientHandler) {
                this.server = server;
                this.client = clientHandler;
            }

            public TCPServer getServer() {
                return server;
            }

            public TCPServer.ClientHandler getClient() {
                return client;
            }

        }

        class TCPClientMessageEvent {

            private final TCPServer server;
            private final TCPServer.ClientHandler client;
            private final byte[] data;

            public TCPClientMessageEvent(TCPServer server, TCPServer.ClientHandler clientHandler, byte[] data) {
                this.server = server;
                this.client = clientHandler;
                this.data = data;
            }

            public TCPServer getServer() {
                return server;
            }

            public TCPServer.ClientHandler getClient() {
                return client;
            }

            public byte[] getData() {
                return data;
            }

            public String getMessage() {
                return new String(data);
            }

        }

        class TCPConnectionCloseEvent {

            private final TCPServer server;
            private final TCPServer.ClientHandler client;

            public TCPConnectionCloseEvent(TCPServer server, TCPServer.ClientHandler clientHandler) {
                this.server = server;
                this.client = clientHandler;
            }

            public TCPServer getServer() {
                return server;
            }

            public TCPServer.ClientHandler getClient() {
                return client;
            }

        }

        class TCPClientConnectionCloseEvent {

            private final TCPServer server;
            private final TCPServer.ClientHandler client;

            public TCPClientConnectionCloseEvent(TCPServer server, TCPServer.ClientHandler clientHandler) {
                this.server = server;
                this.client = clientHandler;
            }

            public TCPServer getServer() {
                return server;
            }

            public TCPServer.ClientHandler getClient() {
                return client;
            }

        }

        class TCPClientConnectionDisconnectEvent {

            private final TCPServer server;
            private final TCPServer.ClientHandler client;

            public TCPClientConnectionDisconnectEvent(TCPServer server, TCPServer.ClientHandler clientHandler) {
                this.server = server;
                this.client = clientHandler;
            }

            public TCPServer getServer() {
                return server;
            }

            public TCPServer.ClientHandler getClient() {
                return client;
            }

        }

        class TCPServerCloseEvent {

            private final TCPServer server;

            public TCPServerCloseEvent(TCPServer server) {
                this.server = server;
            }

            public TCPServer getServer() {
                return server;
            }

        }

    }

}
