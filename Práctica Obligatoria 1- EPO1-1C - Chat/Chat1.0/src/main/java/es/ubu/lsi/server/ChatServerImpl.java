package es.ubu.lsi.server;

import es.ubu.lsi.common.ChatMessage;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

class ChatServerImpl implements ChatServer {
    private int port;
    private boolean running = false;
    private ServerSocket serverSocket;
    private ConcurrentHashMap<Integer, ServerThreadForClient> clients = new ConcurrentHashMap<>();

    public ChatServerImpl(int port) {
        this.port = port;
    }

    @Override
    public void startup() throws IOException {
        serverSocket = new ServerSocket(port);
        running = true;
        System.out.println("Servidor de Chat Iniciado en puerto " + port);

        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                ServerThreadForClient clientThread = new ServerThreadForClient(clientSocket, this);
                clients.put(clientThread.getClientId(), clientThread);
                clientThread.start();
            } catch (IOException e) {
                if (!running) break;
                System.err.println("Error aceptando conexion de cliente: " + e.getMessage());
            }
        }
    }

    @Override
    public void shutdown() throws IOException {
        running = false;
        for (ServerThreadForClient client : clients.values()) {
            client.disconnectClient();
        }
        if (serverSocket != null) {
            serverSocket.close();
        }
        System.out.println("Servidor Chat Shutdown ...");
    }

    @Override
    public void broadcast(ChatMessage message) throws IOException {
        for (ServerThreadForClient client : clients.values()) {
            client.sendMessage(message);
        }
    }

    @Override
    public void remove(int id) throws IOException {
        ServerThreadForClient client = clients.remove(id);
        if (client != null) {
            client.disconnectClient();
        }
    }

    public class ServerThreadForClient extends Thread {
        private Socket socket;
        private ChatServerImpl server;
        private ObjectOutputStream out;
        private ObjectInputStream in;
        private int clientId;
        private int clientCount = 0;

        public ServerThreadForClient(Socket socket, ChatServerImpl server) {
            this.socket = socket;
            this.server = server;
            this.clientId = ++clientCount;
            try {
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());
            } catch (IOException e) {
                System.err.println("Error configurando streams: " + e.getMessage());
            }
        }

        public void run() {
            try {
                ChatMessage message;
                while ((message = (ChatMessage) in.readObject()) != null) {
                    // Process message based on its type
                    server.broadcast(message);
                }
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error en ServerThreadForClient: " + e.getMessage());
            } finally {
                try {
                    server.remove(clientId);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        public void sendMessage(ChatMessage message) throws IOException {
            out.writeObject(message);
            out.flush();
        }

        public int getClientId() {
            return clientId;
        }

        public void disconnectClient() throws IOException {
            socket.close();
        }
    }
}
