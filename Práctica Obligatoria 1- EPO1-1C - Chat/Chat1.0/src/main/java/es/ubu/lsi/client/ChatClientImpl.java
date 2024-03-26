package es.ubu.lsi.client;

import es.ubu.lsi.common.ChatMessage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Clase que representa un cliente de chat.
 * Implementa la interfaz ChatClient para la comunicación con el servidor de chat.
 * Permite enviar y recibir mensajes, así como gestionar la conexión con el servidor.
 * También ofrece funcionalidades para bloquear y desbloquear usuarios.
 *
 * @author José Luis Caballero MQ
 * @version 1.0
 * Repositorio GitHub: https://github.com/JLCaballeroMQ/UBU_SISTEMAS_DISTRIBUIDOS
 */
public class ChatClientImpl implements ChatClient {
    private static final int DEFAULT_PORT = 1500;
    private Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private String host;
    private String nickname;
    private Set<String> usuariosBloqueados = ConcurrentHashMap.newKeySet();

    /**
     * Crea una nueva instancia de un cliente de chat.
     *
     * @param host La dirección IP o el nombre de dominio del servidor de chat.
     * @param nickname El sobrenombre que el usuario usará en el chat.
     */
    public ChatClientImpl(String host, String nickname) {
        this.host = host != null ? host : "localhost";
        this.nickname = nickname;
    }

    /**
     * Inicia el cliente de chat.
     * Establece la conexión con el servidor, envía el mensaje de inicio de sesión,
     * y comienza a escuchar los mensajes entrantes en un hilo separado.
     * Permite enviar mensajes, bloquear usuarios, desbloquear usuarios y cerrar la conexión.
     *
     * @throws IOException si hay algún error de entrada/salida.
     */
    @Override
    public void start() throws IOException {
        // Establecer conexión con el servidor usando el puerto por defecto
        socket = new Socket(host, DEFAULT_PORT);
        System.out.println("Conectado al servidor de chat en " + host + ":" + DEFAULT_PORT + " como " + nickname);

        output = new ObjectOutputStream(socket.getOutputStream());
        input = new ObjectInputStream(socket.getInputStream());

        // Enviar mensaje de inicio de sesión
        sendMessage(new ChatMessage(0, ChatMessage.MessageType.MESSAGE, nickname + " se ha unido al chat."));

        new Thread(new ChatClientListener()).start();

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                String input = scanner.nextLine();
                if (input.startsWith("ban ")) {
                    String userToBan = input.substring(4);
                    usuariosBloqueados.add(userToBan);
                    System.out.println("Usuario " + userToBan + " ha sido bloqueado.");
                } else if (input.startsWith("unban ")) {
                    String userToUnban = input.substring(6);
                    usuariosBloqueados.remove(userToUnban);
                    System.out.println("Usuario " + userToUnban + " ha sido desbloqueado.");
                } else if (input.equalsIgnoreCase("logout")) {
                    sendMessage(new ChatMessage(0, ChatMessage.MessageType.LOGOUT, "Cliente desconectándose"));
                    break;
                } else {
                    sendMessage(new ChatMessage(0, ChatMessage.MessageType.MESSAGE, input));
                }
            }
        } finally {
            disconnect();
        }
    }

    /**
     * Envía un mensaje al servidor de chat.
     *
     * @param message El mensaje a enviar, encapsulado en un objeto {@link ChatMessage}.
     * @throws IOException Si ocurre un error al enviar el mensaje.
     */
    @Override
    public void sendMessage(ChatMessage message) throws IOException {
        output.writeObject(message);
        output.flush();
    }

    /**
     * Desconecta el cliente del servidor de chat.
     */
    @Override
    public void disconnect() {
        try {
            sendMessage(new ChatMessage(0, ChatMessage.MessageType.LOGOUT, ""));
            if (input != null) input.close();
            if (output != null) output.close();
            if (socket != null) socket.close();
            System.out.println("Desconectado del servidor de chat.");
        } catch (IOException e) {
            System.out.println("Error al desconectar: " + e.getMessage());
        }
    }

    /**
     * Hilo que escucha los mensajes entrantes del servidor de chat.
     * Los mensajes son mostrados en la consola, excepto si el remitente está bloqueado por el cliente.
     */
    private class ChatClientListener implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    ChatMessage message = (ChatMessage) input.readObject();

                    String senderNickname = extractSenderNickname(message);
                    if (!usuariosBloqueados.contains(senderNickname)) {
                        System.out.println(message.getMessage());
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Desconectado del servidor.");
            }
        }

        /**
         * Extrae el sobrenombre del remitente de un mensaje.
         *
         * @param message el mensaje del que se extraerá el remitente.
         * @return el sobrenombre del remitente.
         */
        private String extractSenderNickname(ChatMessage message) {
            // Implementación para extraer el sobrenombre del remitente de un mensaje
            // Asumimos que el sobrenombre del remitente está en el inicio del mensaje seguido por ":"
            String content = message.getMessage();
            int colonIndex = content.indexOf(":");
            if (colonIndex != -1) {
                return content.substring(0, colonIndex);
            }
            return "Anónimo"; // Retornamos "Anónimo" si no podemos extraer el sobrenombre
        }
    }

    /**
     * Método principal para iniciar el cliente de chat desde la línea de comandos.
     *
     * @param args argumentos de la línea de comandos para el nombre de host y el sobrenombre del usuario.
     */
    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : null;
        String nickname = args.length > 1 ? args[1] : "Anónimo";

        try {
            ChatClientImpl client = new ChatClientImpl(host, nickname);
            client.start();
        } catch (IOException e) {
            System.out.println("Error al iniciar el cliente de chat: " + e.getMessage());
            e.printStackTrace();
        }
    }
}