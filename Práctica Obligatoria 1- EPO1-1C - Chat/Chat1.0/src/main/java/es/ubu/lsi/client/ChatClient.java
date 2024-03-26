package es.ubu.lsi.client;

import es.ubu.lsi.common.ChatMessage;

import java.io.IOException;

/**
 * Interfaz ChatClient
 * @author José Luis Caballero MQ
 * @Version 1.0
 * Repositorio GitHub: https://github.com/JLCaballeroMQ/UBU_SISTEMAS_DISTRIBUIDOS
 */
public interface ChatClient {

    /**
     * Método para iniciar el cliente de chat
     * @throws IOException
     */
    public void start() throws IOException;

    /**
     * Método para el envio de mensajes
     * @param msg
     * @throws IOException
     */
    public void sendMessage(ChatMessage msg) throws IOException;

    /**
     * Método que cierra todas las conexiones al chat
     * @throws IOException
     */
    public void disconnect() throws IOException;
}
