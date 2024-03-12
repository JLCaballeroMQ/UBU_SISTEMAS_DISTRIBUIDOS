package es.ubu.lsi.client;

import es.ubu.lsi.common.ChatMessage;

import java.io.IOException;

public interface ChatClient {

    public void start() throws IOException;

    public void sendMessage(ChatMessage msg) throws IOException;

    public void disconnect() throws IOException;
}
