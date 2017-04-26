package com.chatserver.server;

import com.chatserver.db.DataStore;

import java.util.Set;
import java.util.stream.Collectors;

public class FriendsManager {

    private OutgoingMessageHandler outgoingMessageHandler = new OutgoingMessageHandler();

    public void addFriend(String userId, String friendUserId, ClientConnection clientConnection) {
        DataStore.getInstance().addFriend(userId, friendUserId);
        //if friend is online, send notification that this user added him/her as friend
        try {
            outgoingMessageHandler.sendMessageToUser("System", friendUserId, userId + " added you as a friend");
        } catch (DeliveryFailureException e) {
            //log error; this message is important so retry and/or send an event to DB/Queue to try later
        }
        clientConnection.send(String.format("user %s added as friend", friendUserId));
        getFriends(userId, clientConnection);
    }

    public void removeFriend(String userId, String friendUserId, ClientConnection clientConnection) {
        DataStore.getInstance().removeFriend(userId, friendUserId);
        //if friend is online, send notification that this user removed him/her as friend
        try {
            outgoingMessageHandler.sendMessageToUser("System", friendUserId, userId+" removed you as a friend");
        } catch (DeliveryFailureException e) {
            //log error; don't care too much telling someone that your friend did not like you, so ignore
        }
        clientConnection.send(String.format("user %s removed as friend", friendUserId));
        getFriends(userId, clientConnection);
    }

    public void getFriends(String userId, ClientConnection clientConnection){
        Set<String> friends = DataStore.getInstance().getFriends(userId);
        if(friends.isEmpty()){
            clientConnection.send("You have no friends");
        }else {
            String friendsString = friends.stream()
                    .map(f -> f + ":" + (DataStore.getInstance().isUserOnline(f) ? "online" : "offline"))
                    .collect(Collectors.joining(", "));
            clientConnection.send("your friends: " + friendsString);
        }
    }

}
