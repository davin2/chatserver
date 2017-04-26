package com.chatserver.db;

import com.chatserver.model.Message;

public interface ChatDataStore {

    /**
     * Save the chat messages. This can be used to send or show offline messages or
     * show the chat history.
     * @param message message to be saved
     */
    public void saveMessage(Message message) throws DatabaseException;

    /**
     * Saves the information that this message has been delivered
     * @param message message to be marked
     */
    public void markDelivered(Message message) throws DatabaseException ;
}
