package es.ubu.lsi.client;

import es.ubu.lsi.common.ChatMessage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ChatClientImpl implements ChatClient {
    private static final int DEFAULT_PORT = 1500;
    private Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private String host;
    private String nickname;
    private Set<String> usuariosBloqueados = ConcurrentHashMap.newKeySet();

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
        // Establecer conexion con el servidor usando el puerto por defecto
        socket = new Socket(host, DEFAULT_PORT);
        System.out.println("Connectado al chat server en " + host + ":" + DEFAULT_PORT + " a " + nickname);

        output = new ObjectOutputStream(socket.getOutputStream());
        input = new ObjectInputStream(socket.getInputStream());

        // Enviando mensaje de login
        sendMessage(new ChatMessage(0, ChatMessage.MessageType.MESSAGE, nickname + " se ha unido al chat."));

        new Thread(new ChatClientListener()).start();

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                String input = scanner.nextLine();
                if (input.startsWith("ban ")) {
                    String userToBan = input.substring(4);
                    usuariosBloqueados.add(userToBan);
                    System.out.println("Usuario " + userToBan + " ha sido baneado.");
                } else if (input.startsWith("unban ")) {
                    String userToUnban = input.substring(6);
                    usuariosBloqueados.remove(userToUnban);
                    System.out.println("Usuario " + userToUnban + " ha dejado de ser baneado.");
                } else if (input.equalsIgnoreCase("logout")) {
                    sendMessage(new ChatMessage(0, ChatMessage.MessageType.LOGOUT, "Cliente logging out"));
                    break;
                } else {
                    sendMessage(new ChatMessage(0, ChatMessage.MessageType.MESSAGE, input));
                }
            }
        }finally {
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
            System.out.println("Desconectado de servidor de chat.");
        } catch (IOException e) {
            System.out.println("Error desconectando: " + e.getMessage());
        }
    }
    private class ChatClientListener implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    ChatMessage message = (ChatMessage) input.readObject();

                    String senderNickname = extraerNicknameEnviado(message);
                    if (!usuariosBloqueados.contains(senderNickname)) {
                        System.out.println(message.getMessage());
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Desconectado de el servidor.");
            }
        }

        private String extraerNicknameEnviado(ChatMessage message) {

            return ""; // Retorna el nickname extraído
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