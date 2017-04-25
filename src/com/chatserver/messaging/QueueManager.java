package com.chatserver.messaging;

import com.chatserver.model.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This class models a distributed Queuing system such as kafka.
 * Server will register with queue manager to create a new queue on
 * which server would listen for messages sent to clients connected.
 * This helps servers to find out the queue when they have to send the message to
 * a particular user.
 */
public class QueueManager {

    //maximum messages that this queue can have
    private final int queueCapacity = 10000;

    Map<String, BlockingQueue<Message>> queueMap = new HashMap<>();

    private static QueueManager queueManager = new QueueManager();

    private QueueManager(){}

    public static QueueManager getInstance() {
        return queueManager;
    }

    //may be a UUID can be generated and returned instead of taking a queueName
    public void register(String queueName){
        BlockingQueue<Message> queue = new LinkedBlockingQueue<>(queueCapacity);
        //what happens when a server dies? how to re-assign or re-play the queue
        //may be a separate process/service can monitor the servers and re-play
        //the messages when a server dies (replay would mean pushing into the
        //queues where the current destination user is logged on)
        queueMap.put(queueName, queue);
    }

    public BlockingQueue<Message> getQueue(String queueName) {
        return queueMap.get(queueName);
    }
}
