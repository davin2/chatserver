package com.chatserver.server;

import com.chatserver.db.DataStore;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class stores the user's current session which encapsulates the client connection.
 * Client session is created when the user logs in and removed at logout time.
 */
public class ClientSessionManager {

    private static ClientSessionManager instance = new ClientSessionManager();
    private ClientSessionManager(){}

    private Map<String, ChatSession> chatSessions = new ConcurrentHashMap<>();

    public static ClientSessionManager getInstance(){
        return instance;
    }

    public void addSession(String userId, ChatSession session){
        //does not handle logging in from multiple windows/clients
        //assume user logs on from one client window only
        chatSessions.put(userId, session);
        //store session info in DB so that other users can find and send message to this user
        DataStore.getInstance().addSession(userId, ChatServer.queueName);
    }

    public void removeSession(String userId){
        chatSessions.remove(userId);
        //remove session info from DB which would mean the user is offline
        //assumption - message can be sent only to online users;
        //no ability to store and forward messages to offline users
        DataStore.getInstance().removeSession(userId);
    }

    public ChatSession getSession(String userId){
        return chatSessions.get(userId);
    }

}
