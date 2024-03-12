package es.ubu.lsi.server;

import es.ubu.lsi.common.ChatMessage;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServerImpl implements ChatServer {
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
}
