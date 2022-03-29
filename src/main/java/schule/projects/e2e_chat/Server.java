package schule.projects.e2e_chat;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Server implements TCPServer.TCPServerHandler {

    private final List<TCPServer> servers = new ArrayList<>();
    private final long p;
    private final long s;

    public Server(long p, long s) {
        this.p = p;
        this.s = s;
    }

    public Server() {
        this.p = Util.getRandomPrime();
        this.s = Util.getRandomBelow(p);
        System.out.println("Server's P: " + p);
        System.out.println("Server's S: " + s);
    }

    public long getP() {
        return p;
    }
    public long getS() {
        return s;
    }

    public void listen(int port) {
        TCPServer server = new TCPServer(port, this);
        servers.add(server);
        server.start();
    }

    @Override
    public void handleConnection(TCPConnectionEvent e) {
        System.out.println("Client connected from " + e.getClient().getSocket().getInetAddress());
    }

    @Override
    public void handleDisconnect(TCPClientConnectionDisconnectEvent e) {
        System.out.println("Client disconnected from " + e.getClient().getSocket().getInetAddress());
    }

    @Override
    public void handleMessage(TCPClientMessageEvent e) {
        System.out.println("Receiving message " + e.getMessage() + " from " + e.getClient().getSocket().getInetAddress());
        handleData(e.getClient(), e.getData());
    }

    private void handleData(TCPServer.ClientHandler client, byte[] data) {
        int pos = 0;
        switch (data[0]) {
            case Util.ByteTags.TAG_GET_P -> {
                ByteBuffer bb = ByteBuffer.allocate(9);
                bb.put(Util.ByteTags.TAG_GET_P);
                bb.putLong(1, getP());
                send(client, bb.array());
                pos = 1;
            }
            case Util.ByteTags.TAG_GET_S -> {
                ByteBuffer bb = ByteBuffer.allocate(9);
                bb.put(Util.ByteTags.TAG_GET_S);
                bb.putLong(1, getS());
                send(client, bb.array());
                pos = 1;
            }
            case Util.ByteTags.TAG_GET_PS -> {
                ByteBuffer bb = ByteBuffer.allocate(17);
                bb.put(Util.ByteTags.TAG_GET_PS);
                bb.putLong(1, getP());
                bb.putLong(9, getS());
                send(client, bb.array());
                pos = 1;
            }
            default -> sendError(client, "ERROR: Unknown Tag sent");
        }

        if(pos < data.length) {
            byte[] sarr = new byte[data.length - pos];
            System.arraycopy(data, pos, sarr, 0, sarr.length);
            handleData(client, sarr);
        }
    }

    private void sendError(TCPServer.ClientHandler client, String message) {
        ByteBuffer bb = ByteBuffer.allocate(5 + message.length());
        bb.put(Util.ByteTags.TAG_ERROR);
        bb.putInt(1, message.length());
        bb.put(5, message.getBytes(StandardCharsets.UTF_8));
        send(client, bb.array());
    }

    private void send(TCPServer.ClientHandler client, byte[] msg) {
        try {
            client.send(msg);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void handleClientClose(TCPClientConnectionCloseEvent e) {
        System.out.println("Client connection closed from " + e.getClient().getSocket().getInetAddress());
    }

    @Override
    public void handleServerClose(TCPServerCloseEvent e) {
        System.out.println("Server closed");
    }
}
