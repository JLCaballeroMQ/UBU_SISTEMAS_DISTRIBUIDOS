package es.ubu.lsi.server;

import es.ubu.lsi.common.ChatMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ChatServerImpl implements ChatServer {
    private int port;
    private boolean running = false;
    private ServerSocket serverSocket;
    private final ConcurrentHashMap<Integer, ServerThreadForClient> clientThreads = new ConcurrentHashMap<>();
    private final AtomicInteger clientIdGenerator = new AtomicInteger(0);

    public ChatServerImpl(int port) {
        this.port = port;
    }

    @Override
    public void startup() throws IOException {
        running = true;
        serverSocket = new ServerSocket(port);
        System.out.println("Servidor Chat ejecutandose en puerto: " + port);

        try {
            while (running) {
                Socket clientSocket = serverSocket.accept();
                int clientId = clientIdGenerator.incrementAndGet();
                ServerThreadForClient clientThread = new ServerThreadForClient(clientSocket, clientId);
                clientThreads.put(clientId, clientThread);
                clientThread.start();
            }
        } finally {
            if (serverSocket != null) {
                serverSocket.close();
            }
        }
    }

    @Override
    public void shutdown() throws IOException {
        running = false;
        for (ServerThreadForClient clientThread : clientThreads.values()) {
            clientThread.interrupt();
        }
        if (!serverSocket.isClosed()) {
            serverSocket.close();
        }
        System.out.println("Servidor Chat apagandose.");
    }

    @Override
    public void broadcast(ChatMessage message) {
        for (ServerThreadForClient clientThread : clientThreads.values()) {
            clientThread.sendMessage(message);
        }
    }

    @Override
    public void remove(int id) {
        ServerThreadForClient clientThread = clientThreads.remove(id);
        if (clientThread != null) {
            clientThread.interrupt();
        }
    }
    private class ServerThreadForClient extends Thread {
        private Socket socket;
        private ObjectOutputStream out;
        private ObjectInputStream in;
        private final int clientId;

        public ServerThreadForClient(Socket socket, int clientId) {
            this.socket = socket;
            this.clientId = clientId;
            try {
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());
            } catch (IOException e) {
                System.out.println("Error configurando streams cliente: " + e.getMessage());
            }
        }

        public void run() {
            try {
                ChatMessage messageInput;
                while ((messageInput = (ChatMessage) in.readObject()) != null) {
                    switch (messageInput.getType()) {
                        case MESSAGE:
                        case LOGOUT:
                            broadcast(messageInput);
                            if (messageInput.getType() == ChatMessage.MessageType.LOGOUT) {
                                return;
                            }
                            break;
                        default:
                            System.out.println("Unhandled message type: " + messageInput.getType());
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Cliente desconectado: " + clientId);
            } finally {
                remove(clientId);
                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println("Error cerrando socket de cliente: " + e.getMessage());
                }
            }
        }

        public void sendMessage(ChatMessage message) {
            try {
                out.writeObject(message);
                out.flush();
            } catch (IOException e) {
                System.out.println("Error enviando mensaje al cliente " + clientId + ": " + e.getMessage());
            }
        }
    }
    public static void main(String[] args) {
        int port = 1500; // Default port
        ChatServerImpl server = new ChatServerImpl(port);
        try {
            server.startup();
        } catch (IOException e) {
            System.out.println("Falla al iniciar el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}