package com.chatserver.server;

/**
 * Represents a connection between server and client
 * For desktop version, we are using Socket based implementation
 * but that could be changed to a web-socket based for example.
 */
public interface ClientConnection {

    /**
     * Send the given message to client
     * @param msg message to send
     */
    public void send(String msg);

    /**
     * Receives a message from the client
     * @param msg message received from client
     */
    public void receive(String msg);

    /**
     * closes the connection
     */
    public void close();
}
