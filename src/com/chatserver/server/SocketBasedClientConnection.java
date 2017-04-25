package com.chatserver.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * This class handles the client's connection using java sockets.
 * A new instance is created when a client connects to server.
 * Delegates all interaction with client to CommandHandler
 */
public class SocketBasedClientConnection implements ClientConnection, Runnable {

    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private CommandHandler commandHandler;


    public SocketBasedClientConnection(Socket clientSocket){
        this.clientSocket = clientSocket;
        this.commandHandler = new CommandHandler();
        try {
            this.out = new PrintWriter(clientSocket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        }catch (Exception e){
            if(out != null) out.close();
            if(in != null) try {
                in.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    public void send(String msg){
        out.println(msg);
    }

    public void receive(String msg){
        commandHandler.handleCommand(msg, this);
    }

    public void close(){
        try{
            if(in != null) in.close();
            if(out != null) out.close();
            if(clientSocket != null) clientSocket.close();
        }catch (Exception e1){}
    }

    @Override
    public void run() {
        out.println("connected to server, please login. Usage: Login|userId ");
        String input;
        try {
            while ((input = in.readLine()) != null) {
                receive(input);
            }
        } catch (Exception e) {
            close();
        }
    }
}
