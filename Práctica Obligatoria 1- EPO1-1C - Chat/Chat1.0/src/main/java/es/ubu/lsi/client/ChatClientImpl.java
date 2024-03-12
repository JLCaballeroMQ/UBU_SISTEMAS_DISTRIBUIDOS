package es.ubu.lsi.client;

import es.ubu.lsi.common.ChatMessage;
import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

public class ChatClientImpl implements ChatClient {
    private String host;
    private int port;
    private Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream output;

    /**
     * Constructor.
     *
     * @param host the server host.
     * @param port the server port.
     */
    public ChatClientImpl(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void start() throws IOException {

        socket = new Socket(host, port);
        System.out.println("Conectado al servidor de Chat");

        output = new ObjectOutputStream(socket.getOutputStream());
        input = new ObjectInputStream(socket.getInputStream());
    }

    @Override
    public void sendMessage(ChatMessage message) throws IOException {
        output.writeObject(message);
        output.flush();
    }

    @Override
    public void disconnect() throws IOException {
        if (input != null) input.close();
        if (output != null) output.close();
        if (socket != null) socket.close();
        System.out.println("Desconectado de el Servidor de chat");
    }
}
