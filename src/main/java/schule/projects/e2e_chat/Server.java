package schule.projects.e2e_chat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Server implements TCPServer.TCPServerHandler {

    private final List<TCPServer> servers = new ArrayList<>();
    private final long p;
    private final long q;
    private boolean started = false;

    public Server(long p, long q) {
        this.p = p;
        this.q = q;
    }

    public Server() {
        this.p = Util.getRandomPrime();
        this.q = Util.getRandomBelow(p);
    }

    public long getP() {
        return p;
    }
    public long getQ() {
        return q;
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
        try {
            e.getClient().send("Nachricht erhalten");
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
