package es.ubu.lsi.server;

import es.ubu.lsi.common.ChatMessage;

import java.io.IOException;

/**
 * Interfaz ChatServer
 * @author José Luis Caballero MQ
 * @Version 1.0
 * Repositorio GitHub: https://github.com/JLCaballeroMQ/UBU_SISTEMAS_DISTRIBUIDOS
 */
public interface ChatServer {
    public void startup() throws IOException;

    public void shutdown() throws IOException;

    public void broadcast(ChatMessage message) throws IOException;

    public void remove(int id) throws IOException;
}
