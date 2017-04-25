package com.chatserver.model;

public class Message {

    private final String fromUserId;
    private final String toUserId;
    private final String message;


    public Message(String fromUserId, String toUserId, String message) {
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.message = message;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public String getToUserId() {
        return toUserId;
    }

    public String getMessage() {
        return message;
    }


}
