package com.chatserver.server;

public class AuthHandler {

    public boolean login(String userId, ClientConnection clientConnection){
        if(isAuthenticated(userId)) {
            //add session so that user is considered online and other users can send message to this user
            ClientSessionManager.getInstance().addSession(userId, new ChatSession(clientConnection));
            System.out.println(String.format("user %s logged in", userId));
            clientConnection.send("Login successful. supported commands are:" +
                    " [Login|<userId>, Send|<userId>|<message>, AddFriend|<userId>," +
                    " RemoveFriend|<userId>, GetFriends, Logout].");
            return true;
        }else {
            clientConnection.send("Login failed. Please try again");
            return false;
        }
    }

    public void logout(String userId, ClientConnection clientConnection) {
        clientConnection.send("logging out...");
        //remove session - this means user is offline
        ClientSessionManager.getInstance().removeSession(userId);
        clientConnection.close();
        System.out.println(String.format("user %s logged out", userId));
    }

    public boolean isAuthenticated(String userId){
        //assume user is authenticated
        return true;
    }

}
