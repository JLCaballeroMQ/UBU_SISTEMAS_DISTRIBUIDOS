package es.ubu.lsi.server;

import es.ubu.lsi.common.ChatMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerThreadForClient extends Thread {
    private Socket socket;
    private ChatServerImpl server;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private int clientId;
    private static int clientCount = 0;

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