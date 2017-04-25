package com.chatserver.server;

/**
 * This class handles the client session.
 * A new instance is created when a client connects to server.
 * Handles the various commands (actions) sent by user such as login,
 * logout, add friend, send message, etc.
 * User has to login to do other actions such as send message. Once the user
 * logs in, session is added to a session manager which saves the location
 * of this user into DB and lets other user find and send messages to this user.
 * After the login, user is considered online, otherwise offline.
 *
 * Since this is a command line client, using pipe ("|") separated string
 * to send commands for simplicity.
 * This means pipe character cannot be used in any of the fields. In real
 * application, probably use some structured format such as json or xml.
 * Below are the expected formats for each command:
 *
 * Login - Login|<userId> example: Login|user1
 * Logout - Logout example: Logout
 * Send - Send|<userId>|<message> example: Send|user2|hello there
 * AddFriend - AddFriend|<userId> example: AddFriend|user2
 * RemoveFriend - RemoveFriend|<userId> example: RemoveFriend|user2
 * GetFriends - GetFriends example: GetFriends (returns comma separated list of <userId>:<status>)
 */
public class CommandHandler {

    private String userId;
    private AuthHandler authHandler = new AuthHandler();
    private FriendsManager friendsManager = new FriendsManager();
    private OutgoingMessageHandler outgoingMessageHandler = new OutgoingMessageHandler();

    public void handleCommand(String input, ClientConnection clientConnection){
        String[] tokens = input.split("\\|");
        String command = tokens[0];
        //force the user to login before doing any other action
        //otherwise could be used to allow anonymous chats
        if(userId == null && Command.fromString(command) != Command.LOGIN ){
            clientConnection.send("Unauthorized - please login.");
            return;
        }
        switch (Command.fromString(command)){
            case LOGIN:
                if(tokens.length != 2) clientConnection.send("not enough arguments - userId missing");
                else {
                    if(authHandler.login(tokens[1], clientConnection)){
                        userId = tokens[1];
                    }
                }
                break;
            case SEND:
                if(tokens.length != 3) clientConnection.send("not enough arguments - userId and/or message missing");
                else outgoingMessageHandler.sendMessage(userId, tokens[1], tokens[2], clientConnection);
                break;
            case ADD_FRIEND:
                if(tokens.length != 2) clientConnection.send("not enough arguments - friend's userId is missing");
                else friendsManager.addFriend(userId, tokens[1], clientConnection);
                break;
            case REMOVE_FRIEND:
                if(tokens.length != 2) clientConnection.send("not enough arguments - friend's userId is missing");
                else friendsManager.removeFriend(userId, tokens[1], clientConnection);
                break;
            case GET_FRIENDS:
                friendsManager.getFriends(userId, clientConnection);
                break;
            case LOGOUT:
                authHandler.logout(userId, clientConnection);
                return;
            default:
                clientConnection.send("could not understand command, supported commands are:" +
                        " [Login|<userId>, Send|<userId>|<message>, AddFriend|<userId>," +
                        " RemoveFriend|<userId>, GetFriends, Logout].");
        }
    }

    //User actions supported by command line client
    private enum Command {
        LOGIN("Login"), LOGOUT("Logout"), SEND("Send"), ADD_FRIEND("AddFriend"),
        REMOVE_FRIEND("RemoveFriend"), GET_FRIENDS("GetFriends"), OTHER("unknown");

        private final String value;

        Command(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return value;
        }

        public static Command fromString(String value) {
            //probably could use a map
            for (Command c : values()) {
                if (c.getValue().equalsIgnoreCase(value)) return c;
            }

            return OTHER;
        }
    }

}