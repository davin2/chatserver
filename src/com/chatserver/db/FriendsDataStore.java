package com.chatserver.db;

import java.util.Set;

/**
 * Stores the information about user's friends.
 */
public interface FriendsDataStore {

    /**
     * Adds a friend
     * @param userId user who is adding friend
     * @param friendUserId user who is being added as a friend
     */
    public void addFriend(String userId, String friendUserId);

    /**
     * Remove a friend
     * @param userId user who is removing friend
     * @param friendUserId user who is being removed as a friend
     */
    public void removeFriend(String userId, String friendUserId);

    /**
     * Get all friends of a given user
     * @param userId user whose friends are to be fetched
     * @return Set of users who are friends to given user
     */
    public Set<String> getFriends(String userId);
}
