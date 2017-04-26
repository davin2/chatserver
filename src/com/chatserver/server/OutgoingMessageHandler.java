package com.chatserver.server;

import com.chatserver.db.DataStore;
import com.chatserver.db.DatabaseException;
import com.chatserver.messaging.QueueManager;
import com.chatserver.model.Message;

public class OutgoingMessageHandler {

    public void sendMessage(String userId, String toUserId, String msg, ClientConnection clientConnection) {
        try {
            if(sendMessageToUser(userId, toUserId, msg)){
                clientConnection.send("your message has been delivered to user: " + toUserId);
            }else {
                clientConnection.send("could not send message, user may be offline");
            }
        } catch (DeliveryFailureException e) {
            clientConnection.send("could not send message, server error, try again");
        }
    }

    public boolean sendMessageToUser(String fromUserId, String toUserId, String msg) throws DeliveryFailureException {
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
                throw new DeliveryFailureException("Could not send message");
            } catch (DatabaseException de){
                throw new DeliveryFailureException(String.format("Could not send message, error message: %s", de.getMessage()));
            }
        }
        return false;
    }

}
