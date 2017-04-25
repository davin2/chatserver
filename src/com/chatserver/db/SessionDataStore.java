package com.chatserver.db;

/**
 * Stores the session by userId
 * When a user logs in, session is created and saved in DB. Session gives the information
 * about on which server machine the user is currently connected. When a user sends a message
 * to another user, server instance of source user looks up the destination user's session
 * in DB and pushes the message in that queue. Destination server is monitoring its own queue
 * and consumes the message and sends it to destination user.
 * This provides flexibility to scale the servers independently of each other.
 */
public interface SessionDataStore {

    /**
     * Stores the session information when a user logs in.
     *
     * @param userId userId of the user
     * @param session session information
     */
    public void addSession(String userId, String session);

    /**
     * Removes the session information when a user logs out.
     *
     * @param userId userId of the user
     */
    public void removeSession(String userId);

    /**
     * Gets the session information of a user.
     *
     * @param userId userId of the user
     * @return session information of the user if available, otherwise null
     */
    public String getSession(String userId);

}
