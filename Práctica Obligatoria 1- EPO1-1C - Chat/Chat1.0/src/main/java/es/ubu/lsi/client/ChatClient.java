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

    public void start() throws IOException;

    public void sendMessage(ChatMessage msg) throws IOException;

    public void disconnect() throws IOException;
}
