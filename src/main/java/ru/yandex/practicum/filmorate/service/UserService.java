package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Service
public class UserService {

    private final UserStorage userStorage;
    private final FriendshipStorage friendshipStorage;

    @Autowired
    public UserService(@Qualifier("userDBStorage") UserStorage userStorage,
                       @Qualifier("friendshipDBStorage") FriendshipStorage friendshipStorage) {
        this.userStorage = userStorage;
        this.friendshipStorage = friendshipStorage;
    }

    public UserDto createUser(NewUserRequest request) {
        User user = UserMapper.mapToUser(request);
        user.setName(user.getName() == null ? user.getLogin() : user.getName());
        return UserMapper.mapToDto(userStorage.save(user));
    }

    public Collection<UserDto> getAllUsers() {
        return userStorage.getUsers().stream()
                .map(UserMapper::mapToDto)
                .toList();
    }

    public UserDto getUser(Long id) {
        return userStorage.getUser(id)
                .map(UserMapper::mapToDto)
                .orElseThrow(() -> new NotFoundException("Пользователь c Id " + id + " не найден"));
    }

    public UserDto updateUser(UpdateUserRequest request) {
        User updatedUser = userStorage.getUser(request.getId())
                .map(user -> UserMapper.updateUserFields(user, request))
                .orElseThrow(() -> new NotFoundException("Пользователь c Id " + request.getId() + " не найден"));
        updatedUser.setName(updatedUser.getName() == null ? updatedUser.getLogin() : updatedUser.getName());
        return UserMapper.mapToDto(userStorage.update(updatedUser));
    }

    public void addFriendToUser(Long userId, Long friendId) {
        User user = userStorage.getUser(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь c Id " + userId + " не найден"));
        User friend = userStorage.getUser(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь c Id " + friendId + " не найден"));

        friendshipStorage.saveFriends(user.getId(), friend.getId());
    }

    public Collection<UserDto> getUserFriends(Long userId) {
        User user = userStorage.getUser(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь c Id " + userId + " не найден"));

        return friendshipStorage.getUserFriends(user.getId()).stream()
                .map(UserMapper::mapToDto)
                .toList();
    }

    public Collection<UserDto> getCommonFriends(Long firstUserId, Long secondUserId) {
        User firstUser = userStorage.getUser(firstUserId)
                .orElseThrow(() -> new NotFoundException("Пользователь c Id " + firstUserId + " не найден"));
        User secondUser = userStorage.getUser(secondUserId)
                .orElseThrow(() -> new NotFoundException("Пользователь c Id " + secondUserId + " не найден"));

        return friendshipStorage.getCommonFriends(firstUser.getId(), secondUser.getId()).stream()
                .map(UserMapper::mapToDto)
                .toList();
    }

    public void deleteFriendFromUser(Long userId, Long friendId) {
        User user = userStorage.getUser(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь c Id " + userId + " не найден"));
        User friend = userStorage.getUser(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь c Id " + friendId + " не найден"));

        friendshipStorage.deleteFriend(user.getId(), friend.getId());
    }

}
