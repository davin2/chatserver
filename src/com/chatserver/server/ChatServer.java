package com.chatserver.server;

import com.chatserver.messaging.QueueManager;

import java.net.*;
import java.io.*;
import java.util.concurrent.*;

/**
 * This class models chat server.
 * In its simplest implementation, it opens a new server socket and listens
 * for incoming client connections. A new chat session is created for every
 * new client connection.
 */
public class ChatServer {

    private final int maxClients = 20;
    //Thread pool to handle the client sessions
    //Supports up to 'maxClients' client connections as we are using blocking sockets
    //in real application, use non-blocking web server such as netty based Play Framework
    private final Executor pool = Executors.newFixedThreadPool(maxClients);
    private final int port;
    //this is used to identify the queue name which this server monitors
    //see QueueManager and DataStore for more info
    //ideally this would be returned by queuing system or generate a UUID
    public static final String queueName = "server-1";

    public ChatServer(int port){
        this.port = port;
        QueueManager.getInstance().register(queueName);
        //multiple consumers can be used to consume messages, creating one for now
        pool.execute(new QueueListener(queueName, new IncomingMessageHandler()));
    }

    private void run(){
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Started ChatServer...Listening on port "+port);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        while(true){
            Socket clientSocket = null;
            try {
                clientSocket = serverSocket.accept();
                System.out.println("received a new connection ");
            } catch (IOException e) {
                e.printStackTrace();
            }
            pool.execute(new SocketBasedClientConnection(clientSocket));
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java ChatServer <port>");
            System.exit(1);
        }
        System.out.println("Starting ChatServer...");
        ChatServer server = new ChatServer(Integer.parseInt(args[0]));
        server.run();
    }
}
