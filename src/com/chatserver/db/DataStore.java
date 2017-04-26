package com.chatserver.db;

import com.chatserver.model.Message;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * This class models the persistent database (such as Cassandra) which should store the user's information
 * such as user info, user's friends and user's active session.
 *
 * This is an in-memory implementation of friends and session data stores.
 */
public class DataStore implements FriendsDataStore, SessionDataStore, ChatDataStore {

    private DataStore(){}

    private static final DataStore instance = new DataStore();

    //stores the friends list by userId
    private Map<String, Set<String>> friends = new ConcurrentHashMap<>();

    private Map<String, String> sessions = new ConcurrentHashMap<>();

    public static DataStore getInstance(){
        return instance;
    }

    public void addFriend(String userId, String friendUserId){
        //cannot add self as friend
        if(userId.equalsIgnoreCase(friendUserId)) return;

        if(!friends.containsKey(userId)){
            friends.putIfAbsent(userId, new CopyOnWriteArraySet<>());
        }
        if(!friends.containsKey(friendUserId)){
            friends.putIfAbsent(friendUserId, new CopyOnWriteArraySet<>());
        }

        friends.get(userId).add(friendUserId);
        friends.get(friendUserId).add(userId);
    }

    public void removeFriend(String userId, String friendUserId){
        if(friends.containsKey(userId)){
            friends.get(userId).remove(friendUserId);
        }
        if(friends.containsKey(friendUserId)){
            friends.get(friendUserId).remove(userId);
        }
    }

    public Set<String> getFriends(String userId){
        if(friends.containsKey(userId)){
            return new HashSet<>(friends.get(userId));
        }

        return Collections.emptySet();
    }

    public void addSession(String userId, String serverQueue){
        sessions.put(userId, serverQueue);
    }

    public void removeSession(String userId){
        sessions.remove(userId);
    }

    public String getSession(String userId){
        return sessions.get(userId);
    }

    /**
     * User is online if there is a session in the database, otherwise offline
     */
    public boolean isUserOnline(String userId){
        return getSession(userId) != null;
    }


    public void saveMessage(Message message) throws DatabaseException {
        //save this message to DB
        //doing nothing for now
    }

    public void markDelivered(Message message) throws DatabaseException {
        //mark the message as delivered
        //ignoring for now
    }
}
