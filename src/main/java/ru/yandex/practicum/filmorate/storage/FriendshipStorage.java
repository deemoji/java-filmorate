package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface FriendshipStorage {
    void saveFriends(Long userIdOne, Long userIdTwo);

    Collection<User> getUserFriends(Long userId);

    Collection<User> getCommonFriends(Long userIdOne, Long userIdTwo);

    void deleteFriend(Long userId, Long friendId);
}
