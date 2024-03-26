package es.ubu.lsi.server;

import es.ubu.lsi.common.ChatMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Clase ChatServerImpl implementacion de interfaz ChatServer
 * @author José Luis Caballero MQ
 * @Version 1.0
 * Repositorio GitHub: https://github.com/JLCaballeroMQ/UBU_SISTEMAS_DISTRIBUIDOS
 */

/**
 * Implementación del servidor de chat.
 * Gestiona la conexión de clientes al servidor, permite enviar mensajes a todos los clientes
 * y maneja la desconexión de clientes de forma segura.
 */
public class ChatServerImpl implements ChatServer {
    private int port;
    private boolean running = false;
    private ServerSocket serverSocket;
    private final ConcurrentHashMap<Integer, ServerThreadForClient> clientThreads = new ConcurrentHashMap<>();
    private final AtomicInteger clientIdGenerator = new AtomicInteger(0);

    /**
     * Constructor que inicializa el servidor en un puerto específico.
     *
     * @param port Puerto en el que el servidor aceptará conexiones.
     */
    public ChatServerImpl(int port) {
        this.port = port;
    }

    /**
     * Inicia el servidor para que comience a aceptar conexiones de clientes.
     * Crea un ServerSocket y entra en un bucle que espera conexiones de clientes para atenderlas.
     *
     * @throws IOException Si ocurre un error al intentar iniciar el servidor o aceptar conexiones.
     */
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

    /**
     * Detiene el servidor cerrando todas las conexiones activas y liberando recursos.
     *
     * @throws IOException Si ocurre un error al cerrar el servidor o las conexiones de clientes.
     */
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

    /**
     * Envia un mensaje a todos los clientes conectados al servidor.
     *
     * @param message El mensaje a enviar a todos los clientes.
     */
    @Override
    public void broadcast(ChatMessage message) {
        for (ServerThreadForClient clientThread : clientThreads.values()) {
            clientThread.sendMessage(message);
        }
    }

    /**
     * Elimina a un cliente de la lista de clientes activos, usualmente debido a una desconexión.
     *
     * @param id El ID único del cliente a eliminar.
     */
    @Override
    public void remove(int id) {
        ServerThreadForClient clientThread = clientThreads.remove(id);
        if (clientThread != null) {
            clientThread.interrupt();
        }
    }

    /**
     * Clase interna que gestiona la comunicación con un cliente conectado.
     * Lee mensajes del cliente y los retransmite al resto de clientes conectados.
     */
    private class ServerThreadForClient extends Thread {
        private Socket socket;
        private ObjectOutputStream out;
        private ObjectInputStream in;
        private final int clientId;

        /**
         * Constructor que inicializa el hilo de atención al cliente.
         *
         * @param socket Socket de conexión con el cliente.
         * @param clientId ID único asignado al cliente.
         */
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

        /**
         * Se ejecuta el hilo que lee todos los mensajes
         */
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

        /**
         * Envio de mensajes para todos los usuarios
         * @param message
         */
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