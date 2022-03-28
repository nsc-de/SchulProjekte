package schule.projects.e2e_chat;

/*
public class SimpleTCPServer {

    private final List<TCPServer> servers = new ArrayList<>();
    private final List<Consumer<TCPServerHandler.TCPConnectionEvent>> connectionEventListeners = new ArrayList<>();
    private final List<Consumer<TCPServerHandler.TCPClientMessageEvent>> messageEventListeners = new ArrayList<>();
    private final List<Consumer<TCPServerHandler.TCPClientConnectionDisconnectEvent>> disconnectionEventListeners = new ArrayList<>();
    private final List<Consumer<TCPServerHandler.TCPClientConnectionCloseEvent>> closeEventListeners = new ArrayList<>();
    private final List<Consumer<TCPServerHandler.TCPServerCloseEvent>> serverCloseEventListeners = new ArrayList<>();

    public SimpleTCPServer() {}

    public void listen(int port) {
        TCPServer server = new TCPServer(port, new EventHandler());
        server.start();
        this.servers.add(server);
    }

    public void close() {
        this.servers.forEach(it -> {
            try {
                it.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void onConnectionEvent(Consumer<TCPServerHandler.TCPConnectionEvent> listener) {
        this.connectionEventListeners.add(listener);
    }

    public void onMessageEvent(Consumer<TCPServerHandler.TCPClientMessageEvent> listener) {
        this.messageEventListeners.add(listener);
    }

    public void onDisconnectionEvent(Consumer<TCPServerHandler.TCPClientConnectionDisconnectEvent> listener) {
        this.disconnectionEventListeners.add(listener);
    }

    public void onClientConnectionCloseEvent(Consumer<TCPServerHandler.TCPClientConnectionCloseEvent> listener) {
        this.closeEventListeners.add(listener);
    }

    public void onServerCloseEvent(Consumer<TCPServerHandler.TCPServerCloseEvent> listener) {
        this.serverCloseEventListeners.add(listener);
    }

    private class EventHandler implements TCPServerHandler {

        @Override
        public void handleConnection(TCPConnectionEvent event) {
            connectionEventListeners.forEach(l -> l.accept(event));
        }

        @Override
        public void handleMessage(TCPClientMessageEvent event) {
            messageEventListeners.forEach(l -> l.accept(event));
        }

        @Override
        public void handleDisconnect(TCPClientConnectionDisconnectEvent event) {
            disconnectionEventListeners.forEach(l -> l.accept(event));
        }

        @Override
        public void handleClientClose(TCPClientConnectionCloseEvent event) {
            closeEventListeners.forEach(l -> l.accept(event));
        }

        @Override
        public void handleServerClose(TCPServerCloseEvent event) {
            serverCloseEventListeners.forEach(l -> l.accept(event));
        }
    }

}
*/