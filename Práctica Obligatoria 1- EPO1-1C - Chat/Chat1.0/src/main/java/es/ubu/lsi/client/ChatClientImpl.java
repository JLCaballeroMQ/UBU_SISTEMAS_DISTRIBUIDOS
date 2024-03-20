package es.ubu.lsi.client;

import es.ubu.lsi.common.ChatMessage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class ChatClientImpl implements ChatClient {
    private static final int DEFAULT_PORT = 1500;
    private Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private String host;
    private String nickname;

    /**
     * Constructor.
     *
     * @param host     el servidor host, "localhost" es usado por defecto si no el valor es null.
     * @param nickname el sobrenombre del usuario.
     */
    public ChatClientImpl(String host, String nickname) {
        this.host = host != null ? host : "localhost";
        this.nickname = nickname;
    }

    @Override
    public void start() throws IOException {
        // Establish connection with the server using the default port
        socket = new Socket(host, DEFAULT_PORT);
        System.out.println("Connected to chat server at " + host + ":" + DEFAULT_PORT + " as " + nickname);

        // Setup streams
        output = new ObjectOutputStream(socket.getOutputStream());
        input = new ObjectInputStream(socket.getInputStream());

        // Send a login message
        sendMessage(new ChatMessage(0, ChatMessage.MessageType.MESSAGE, nickname + " has joined the chat."));

        // Start a thread to listen for messages from the server
        new Thread(new ChatClientListener()).start();

        // Read messages from stdin and send to server
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                String messageText = scanner.nextLine();
                if (messageText.equalsIgnoreCase("logout")) {
                    sendMessage(new ChatMessage(0, ChatMessage.MessageType.LOGOUT, "Client logging out"));
                    break;
                }
                sendMessage(new ChatMessage(0, ChatMessage.MessageType.MESSAGE, messageText));
            }
        } finally {
            disconnect();
        }
    }

    @Override
    public void sendMessage(ChatMessage message) throws IOException {
        output.writeObject(message);
        output.flush();
    }

    @Override
    public void disconnect() {
        try {
            sendMessage(new ChatMessage(0, ChatMessage.MessageType.LOGOUT, ""));
            if (input != null) input.close();
            if (output != null) output.close();
            if (socket != null) socket.close();
            System.out.println("Disconnected from chat server.");
        } catch (IOException e) {
            System.out.println("Error disconnecting: " + e.getMessage());
        }
    }
    private class ChatClientListener implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    ChatMessage message = (ChatMessage) input.readObject();
                    System.out.println(message.getMessage());
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Desconectado de el servidor.");
            }
        }
    }

    /**
     * The main method to start the chat client.
     *
     * @param args command-line arguments for hostname and nickname.
     */
    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : null;
        String nickname = args.length > 1 ? args[1] : "Anonimo";

        try {
            ChatClientImpl client = new ChatClientImpl(host, nickname);
            client.start();
        } catch (IOException e) {
            System.out.println("Error iniciando el chat cliente : " + e.getMessage());
            e.printStackTrace();
        }
    }
}