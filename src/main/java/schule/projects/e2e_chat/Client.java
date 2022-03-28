package schule.projects.e2e_chat;

import java.io.IOException;

public class Client implements TCPClient.TCPClientEventHandler {

    private TCPClient client;

    public Client() {
        client = new TCPClient(this);
    }

    public void connect(String host, int port) {
        try {
            client.connect(host, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(byte[] message) {
        try {
            client.send(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(String message) {
        this.send(message.getBytes());
    }

    public void disconnect() {
        try {
            client.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnected(TCPClientConnectedEvent e) {
        System.out.println("Connected to server " + e.getSocket().getInetAddress().getHostAddress() + ":" + e.getSocket().getPort());
    }

    @Override
    public void onDisconnected(TCPClientDisconnectedEvent e) {
        System.out.println("Disconnected from server " + e.getSocket().getInetAddress().getHostAddress() + ":" + e.getSocket().getPort());
    }

    @Override
    public void onData(TCPClientMessageEvent e) {
        System.out.println("Received data from server " + e.getSocket().getInetAddress().getHostAddress() + ":" + e.getSocket().getPort() + ": " + new String(e.getData()));
    }
}
