package es.ubu.lsi.server;

import es.ubu.lsi.common.ChatMessage;

import java.io.IOException;

public interface ChatServer {
    public void startup() throws IOException;

    public void shutdown() throws IOException;

    public void broadcast(ChatMessage message) throws IOException;

    public void remove(int id) throws IOException;
}
