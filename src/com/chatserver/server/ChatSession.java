package com.chatserver.server;

/**
 * Chat session that wraps underlying connection to the client
 */
public class ChatSession {

    private ClientConnection clientConnection;

    public ChatSession(ClientConnection clientConnection) {
        this.clientConnection = clientConnection;
    }

    public void sendMessage(String msg) {
        clientConnection.send(msg);
    }

}
