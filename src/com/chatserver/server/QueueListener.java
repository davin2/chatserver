package com.chatserver.server;

import com.chatserver.messaging.QueueManager;
import com.chatserver.model.Message;

import java.util.concurrent.BlockingQueue;

/**
 * This class models the queue consumer.
 * Listens to this server's queue on the queuing system (such as kafka) and consumes
 * messages as and when they arrive and sends them to destination users.
 */
public class QueueListener implements Runnable {

    private final BlockingQueue<Message> queue;
    private final IncomingMessageHandler incomingMessageHandler;

    public QueueListener(String queueName, IncomingMessageHandler incomingMessageHandler){
        this.queue = QueueManager.getInstance().getQueue(queueName);
        this.incomingMessageHandler = incomingMessageHandler;
    }

    @Override
    public void run() {
        try{
            while(true){
                Message message = queue.take();
                incomingMessageHandler.handleMessage(message);
            }
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
