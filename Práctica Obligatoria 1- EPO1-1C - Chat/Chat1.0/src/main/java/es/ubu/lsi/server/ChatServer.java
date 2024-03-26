package es.ubu.lsi.server;

import es.ubu.lsi.common.ChatMessage;

import java.io.IOException;

/**
 * Define la interfaz para un servidor de chat.
 * Esta interfaz proporciona los métodos necesarios para iniciar y detener el servidor,
 * así como para enviar mensajes a todos los clientes conectados y eliminar a un cliente de la lista activa.
 *
 * @author José Luis Caballero MQ
 * @version 1.0
 * Repositorio GitHub: https://github.com/JLCaballeroMQ/UBU_SISTEMAS_DISTRIBUIDOS
 */
public interface ChatServer {
    /**
     * Inicia el servidor, preparándolo para aceptar conexiones de clientes.
     * Este método debe establecer la conexión de red y comenzar a escuchar las solicitudes de los clientes.
     *
     * @throws IOException Si ocurre un error al intentar iniciar el servidor.
     */
    void startup() throws IOException;

    /**
     * Apaga el servidor, cerrando todas las conexiones activas y liberando los recursos asociados.
     * Este método debe asegurarse de que todos los recursos de red se cierren adecuadamente.
     *
     * @throws IOException Si ocurre un error durante el proceso de cierre del servidor.
     */
    void shutdown() throws IOException;

    /**
     * Envía un mensaje a todos los clientes conectados actualmente al servidor.
     * Este método se utiliza para implementar la funcionalidad de difusión, permitiendo
     * que los mensajes de un cliente sean recibidos por todos los demás.
     *
     * @param message El mensaje que se va a enviar a todos los clientes.
     * @throws IOException Si ocurre un error al enviar el mensaje.
     */
    void broadcast(ChatMessage message) throws IOException;

    /**
     * Elimina a un cliente de la lista de clientes activos, generalmente debido a una desconexión.
     * Este método se utiliza para manejar la desconexión de un cliente, asegurando que el servidor
     * no intente enviar más mensajes a un cliente que ya no está conectado.
     *
     * @param id El identificador único del cliente que se va a eliminar.
     * @throws IOException Si ocurre un error al eliminar al cliente.
     */
    void remove(int id) throws IOException;
}
