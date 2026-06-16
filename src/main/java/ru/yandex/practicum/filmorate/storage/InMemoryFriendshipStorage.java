package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryFriendshipStorage implements FriendshipStorage {

    private final Map<Long, Set<Long>> friends = new HashMap<>();
    private final UserStorage userStorage;

    public InMemoryFriendshipStorage(@Qualifier("inMemoryUserStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public void saveFriends(Long userIdOne, Long userIdTwo) {
        Set<Long> userOneFriends = friends.getOrDefault(userIdOne, new HashSet<>());
        userOneFriends.add(userIdTwo);
        Set<Long> userTwoFriends = friends.getOrDefault(userIdTwo, new HashSet<>());
        userTwoFriends.add(userIdOne);
        friends.put(userIdOne, userOneFriends);
        friends.put(userIdTwo, userTwoFriends);
    }

    @Override
    public Collection<User> getUserFriends(Long userId) {
        return friends.getOrDefault(userId, new HashSet<>())
                .stream().map(userStorage::getUser)
                .map(Optional::get)
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<User> getCommonFriends(Long userIdOne, Long userIdTwo) {
        Set<Long> userOneFriends = friends.getOrDefault(userIdOne, new HashSet<>());
        Set<Long> userTwoFriends = friends.getOrDefault(userIdTwo, new HashSet<>());
        return userOneFriends.stream()
                .filter(userTwoFriends::contains)
                .map(userStorage::getUser)
                .map(Optional::get)
                .collect(Collectors.toSet());
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        Set<Long> userOneFriends = friends.getOrDefault(userId, new HashSet<>());
        Set<Long> userTwoFriends = friends.getOrDefault(friendId, new HashSet<>());

        userOneFriends.remove(friendId);
        userTwoFriends.remove(userId);
    }
}
