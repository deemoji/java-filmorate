package ru.yandex.practicum.filmorate.storage;

import java.util.Collection;

public interface FriendshipStorage {
    void saveFriends(Long userIdOne, Long userIdTwo);
    Collection<Long> getUserFriends(Long userId);
    Collection<Long> getCommonFriends(Long userIdOne, Long userIdTwo);
    void deleteFriend(Long userId, Long friendId);
}
