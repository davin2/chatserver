package com.chatserver.server;

import com.chatserver.db.DataStore;
import com.chatserver.messaging.QueueManager;
import com.chatserver.model.Message;

public class OutgoingMessageHandler {

    public void sendMessage(String userId, String toUserId, String msg, ClientConnection clientConnection) {
        if(sendMessageToUser(userId, toUserId, msg)){
            clientConnection.send("your message has been delivered to user: " + toUserId);
        }else {
            clientConnection.send("could not send message, user may be offline");
        }
    }

    public boolean sendMessageToUser(String fromUserId, String toUserId, String msg) {
        String toUserSession = DataStore.getInstance().getSession(toUserId);

        if(toUserSession != null){
            Message message = new Message(fromUserId, toUserId, msg);
            try {
                DataStore.getInstance().saveMessage(message);
                QueueManager.getInstance().getQueue(toUserSession).put(message);
                return true;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

}
