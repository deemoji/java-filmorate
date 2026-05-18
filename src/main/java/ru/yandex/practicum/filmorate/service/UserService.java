package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.util.Constants;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserStorage userStorage;
    private final FriendshipStorage friendshipStorage;

    @Autowired
    public UserService(UserStorage userStorage, FriendshipStorage friendshipStorage) {
        this.userStorage = userStorage;
        this.friendshipStorage = friendshipStorage;
    }

    public User createUser(User user) {
        user.setId(generateId());
        user.setName(user.getName() == null ? user.getLogin() : user.getName());
        userStorage.saveOrUpdate(user);
        return user;
    }

    public Collection<User> getAllUsers() {
        return userStorage.getUsers();
    }

    public User updateUser(User user) {
        if (userStorage.contains(user.getId())) {
            user.setName(user.getName() == null ? user.getLogin() : user.getName());
            userStorage.saveOrUpdate(user);
            return user;
        }
        throw new NotFoundException("Пользователь c Id " + user.getId() + " не найден");
    }

    public User deleteUser(Long id) {
        if (userStorage.contains(id)) {
            User user = userStorage.getUser(id);
            userStorage.delete(id);
            return user;
        }
        throw new NotFoundException("Пользователь c Id " + id + " не найден");
    }

    public void addFriendToUser(Long userId, Long friendId) {
        if (userStorage.contains(userId)) {
            if (userStorage.contains(friendId)) {
                friendshipStorage.saveFriends(userId, friendId);
                return;
            }
            throw new NotFoundException("Пользователь c Id " + friendId + " не найден");
        }
        throw new NotFoundException("Пользователь c Id " + userId + " не найден");
    }

    public Collection<User> getUserFriends(Long userId) {
        Collection<Long> friends = friendshipStorage.getUserFriends(userId);
        return friends.stream()
                .map(userStorage::getUser)
                .collect(Collectors.toSet());
    }

    public Collection<User> getCommonFriends(Long firstUserId, Long secondUserId) {
        Collection<Long> commonFriends = friendshipStorage.getCommonFriends(firstUserId, secondUserId);
        return commonFriends.stream()
                .map(userStorage::getUser)
                .collect(Collectors.toSet());
    }

    public void deleteFriendFromUser(Long userId, Long friendId) {
        if (userStorage.contains(userId)) {
            if (userStorage.contains(friendId)) {
                friendshipStorage.deleteFriend(userId, friendId);
                return;
            }
            throw new NotFoundException("Пользователь c Id " + friendId + " не найден");
        }
        throw new NotFoundException("Пользователь c Id " + userId + " не найден");
    }

    private Long generateId() {
        long maxId = users.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(Constants.ID_GENERATOR_START_INDEX);
        return ++maxId;
    }
}
