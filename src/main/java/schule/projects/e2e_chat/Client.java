package schule.projects.e2e_chat;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Client implements TCPClient.TCPClientEventHandler {

    private final TCPClient client;
    private long p;
    private long s;

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
        refreshPS();
    }

    @Override
    public void onDisconnected(TCPClientDisconnectedEvent e) {
        System.out.println("Disconnected from server " + e.getSocket().getInetAddress().getHostAddress() + ":" + e.getSocket().getPort());
    }

    @Override
    public void onData(TCPClientMessageEvent e) {
        System.out.println("Received data from server " + e.getSocket().getInetAddress().getHostAddress() + ":" + e.getSocket().getPort());
        handleData(e.getClient(), e.getData());
    }

    private void handleData(TCPClient con, byte[] data) {

        int pos = 0;
        switch (data[0]) {
            case Util.ByteTags.TAG_ERROR -> {
                if(data.length < 5) {
                    System.err.println("ERROR: Received wrong data");
                    return;
                }
                ByteBuffer bb = ByteBuffer.wrap(data);
                int length = bb.getInt(1);
                if(data.length < length + 5) {
                    System.err.println("ERROR: Received wrong data");
                    return;
                }

                byte[] error = new byte[length];
                bb.get(5, error);

                System.err.println("Received Error from server: " + new String(error, StandardCharsets.UTF_8));
                pos = length + 5;
            }
            case Util.ByteTags.TAG_GET_P -> {
                if(data.length < 9) {
                    System.err.println("ERROR: Received wrong data");
                    return;
                }
                ByteBuffer bb = ByteBuffer.wrap(data);
                this.setP(bb.getLong(1));
                pos = 9;
            }
            case Util.ByteTags.TAG_GET_S -> {
                if(data.length < 9) {
                    System.err.println("ERROR: Received wrong data");
                    return;
                }
                ByteBuffer bb = ByteBuffer.wrap(data);
                this.setS(bb.getLong(1));
                pos = 9;
            }

            case Util.ByteTags.TAG_GET_PS -> {
                if(data.length < 17) {
                    System.err.println("ERROR: Received wrong data");
                    return;
                }
                ByteBuffer bb = ByteBuffer.wrap(data);
                this.setP(bb.getLong(1));
                this.setS(bb.getLong(9));
                pos = 17;
            }
        }

        if(pos < data.length) {
            byte[] sarr = new byte[data.length - pos];
            System.arraycopy(data, pos, sarr, 0, sarr.length);
            handleData(con, sarr);
        }
    }

    public void refreshPS() {
        send(new byte[] {Util.ByteTags.TAG_GET_PS});
    }

    public void setP(long p) {
        System.out.println("Refresh to server-p: " + p);
        this.p = p;
    }

    public void setS(long s) {
        System.out.println("Refresh to server-s: " + s);
        this.s = s;
    }

    public long getP() {
        return p;
    }

    public long getS() {
        return s;
    }
}
