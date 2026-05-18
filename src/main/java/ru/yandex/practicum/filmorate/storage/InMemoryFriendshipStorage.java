package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryFriendshipStorage implements FriendshipStorage {

    private final Map<Long, Set<Long>> friends = new HashMap<>();

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
    public Collection<Long> getUserFriends(Long userId) {
        return friends.getOrDefault(userId, new HashSet<>());
    }

    @Override
    public Collection<Long> getCommonFriends(Long userIdOne, Long userIdTwo) {
        Set<Long> userOneFriends = friends.getOrDefault(userIdOne, new HashSet<>());
        Set<Long> userTwoFriends = friends.getOrDefault(userIdTwo, new HashSet<>());
        return userOneFriends.stream()
                .filter(userTwoFriends::contains)
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
