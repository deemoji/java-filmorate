package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public Collection<User> getUsers() {
        return service.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable long id) {
        return service.getUser(id);
    }

    @PostMapping
    public User createUser(@Valid @RequestBody NewUserRequest request) {
        log.info("Создание пользователя: {}", request.getName());
        return service.createUser(request);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody UpdateUserRequest request) {
        log.info("Обновление пользователя: {}", request.getId());
        return service.updateUser(request);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getUserFriends(@PathVariable long id) {
        return service.getUserFriends(id);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public void addFriendToUser(@PathVariable long userId, @PathVariable long friendId) {
        log.info("Добавление пользователю {} в друзья: {}", userId, friendId);
        service.addFriendToUser(userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public void deleteFriendFromUser(@PathVariable long userId, @PathVariable long friendId) {
        log.info("Удаление у пользователя {} друга: {}", userId, friendId);
        service.deleteFriendFromUser(userId, friendId);
    }

    @GetMapping("/{userIdOne}/friends/common/{userIdTwo}")
    public Collection<User> getCommonFriends(@PathVariable long userIdOne, @PathVariable long userIdTwo) {
        return service.getCommonFriends(userIdOne, userIdTwo);
    }

}
