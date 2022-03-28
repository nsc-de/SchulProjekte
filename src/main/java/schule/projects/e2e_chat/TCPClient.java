package schule.projects.e2e_chat;

import java.io.*;
import java.net.Socket;

public class TCPClient extends Thread {
    private Socket clientSocket;
    private OutputStream out;
    private BufferedInputStream in;
    private boolean running;
    private final TCPClientEventHandler handler;

    public TCPClient(TCPClientEventHandler handler) {
        this.handler = handler;
    }

    public void connect(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        out = clientSocket.getOutputStream();
        in = new BufferedInputStream(clientSocket.getInputStream());
        this.start();
        this.handler.onConnected(new TCPClientEventHandler.TCPClientConnectedEvent(this));
    }

    public void send(byte[] bytes) throws IOException {
        out.write(bytes);
    }

    public void send(String msg) throws IOException {
        out.write(msg.getBytes());
    }

    public void disconnect() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }

    public void close() throws IOException {
        running = false;
        disconnect();
    }

    @Override
    public void run() {
        running = true;
        while (running) {
            if(clientSocket.isClosed()) {
                running = false;
                handler.onDisconnected(new TCPClientEventHandler.TCPClientDisconnectedEvent(this));
                break;
            }
            try {
                InputStream inputStream = clientSocket.getInputStream();
                if(inputStream.available() > 0) {
                    int amount = inputStream.available();
                    byte[] buffer = new byte[amount];
                    if(inputStream.read(buffer) != amount) throw new IOException("Could not read all bytes");
                    handler.onData(new TCPClientEventHandler.TCPClientMessageEvent(this, buffer));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public TCPClient getClient() {
        return this;
    }

    public boolean isRunning() {
        return running;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public TCPClientEventHandler getHandler() {
        return handler;
    }

    public static class TCPClientConnection {
        private TCPClient client;

        public TCPClientConnection(TCPClient client) {
            this.client = client;
        }

        public void send(String msg) throws IOException {
            client.send(msg);
        }

        public void send(byte[] bytes) throws IOException {
            client.send(bytes);
        }

        public void close() throws IOException {
            client.close();
        }

        public TCPClient getClient() {
            return client;
        }
    }

    interface TCPClientEventHandler {
        default void onConnected(TCPClientConnectedEvent e) {}
        default void onDisconnected(TCPClientDisconnectedEvent e) {}
        default void onData(TCPClientMessageEvent e) {}

        class TCPClientConnectedEvent {
            private final TCPClient client;

            public TCPClientConnectedEvent(TCPClient client) {
                this.client = client;
            }

            public TCPClient getClient() {
                return client;
            }
            public Socket getSocket() {
                return client.getClientSocket();
            }
        }

        class TCPClientDisconnectedEvent {
            private final TCPClient client;

            public TCPClientDisconnectedEvent(TCPClient client) {
                this.client = client;
            }

            public TCPClient getClient() {
                return client;
            }
            public Socket getSocket() {
                return client.getClientSocket();
            }
        }

        class TCPClientMessageEvent {
            private final TCPClient client;
            private final byte[] data;

            public TCPClientMessageEvent(TCPClient client, byte[] data) {
                this.client = client;
                this.data = data;
            }

            public TCPClient getClient() {
                return client;
            }
            public Socket getSocket() {
                return client.getClientSocket();
            }
            public byte[] getData() {
                return data;
            }
        }


    }
}
