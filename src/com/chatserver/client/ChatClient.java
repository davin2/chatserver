package com.chatserver.client;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

/**
 * This class models chat client.
 * In its simplest implementation, it opens a new client socket and connects
 * to server running on localhost and given port.
 */

public class ChatClient {

    private final ExecutorService pool = Executors.newFixedThreadPool(4);
    private final int portNumber;
    private final String hostName = "localhost";

    public ChatClient(int portNumber){
        this.portNumber = portNumber;
    }

    public void run(){
        try {
            Socket socket = new Socket(hostName, portNumber);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            Future f1 = pool.submit(new Sender(stdIn, out));
            Future f2 = pool.submit(new Receiver(in));
            //wait for future to keep main alive
            f1.get();
        } catch (ConnectException c) {
            System.err.println("Cannot connect to host: "+hostName+", port: "+portNumber);
            c.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

    private static class Sender implements Runnable {
        BufferedReader stdIn;
        PrintWriter out;

        public Sender(BufferedReader stdIn, PrintWriter out){
            this.stdIn = stdIn;
            this.out = out;
        }

        @Override
        public void run() {
            String input;
            try {
                while ((input = stdIn.readLine()) != null) {
                    out.println(input);
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    private static class Receiver implements Runnable {
        BufferedReader in;

        public Receiver(BufferedReader in){
            this.in = in;
        }

        @Override
        public void run() {
            String input;
            try {
                while ((input = in.readLine()) != null) {
                    if(input.contains("logging out")){
                        System.out.println("Logout successful. Closing client.");
                        System.exit(1);
                    }else{
                        System.out.println(input);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java ChatClient <port>");
            System.exit(1);
        }

        ChatClient chatClient = new ChatClient(Integer.parseInt(args[0]));
        chatClient.run();
    }

}
