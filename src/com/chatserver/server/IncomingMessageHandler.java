package com.chatserver.server;

import com.chatserver.db.DataStore;
import com.chatserver.model.Message;

/**
 * Receives an incoming message from the queue and sends
 * it to its destination user if that user if online.
 */
public class IncomingMessageHandler {

    public void handleMessage(Message message){
        ChatSession chatSession = ClientSessionManager.getInstance().getSession(message.getToUserId());
        //assumption - drops the message if user is offline
        //may be send to a store and forward queue or save in DB for later delivery
        if(chatSession != null) {
            chatSession.sendMessage("Message received - from: " + message.getFromUserId() + ", msg: " + message.getMessage());
            DataStore.getInstance().markDelivered(message);
        }
    }
}
