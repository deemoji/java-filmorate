package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getUsers() {
        return users.values();
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("Создание пользователя: {}", user.getName());

        user.setId(generateId());
        user.setName(user.getName() == null ? user.getLogin() : user.getName());
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        log.info("Обновление пользователя: {}", newUser.getName());

        if (!users.containsKey(newUser.getId())) {
            throw new NotFoundException("Пользователь c Id " + newUser.getId() + " не найден");
        }
        newUser.setName(newUser.getName() == null ? newUser.getLogin() : newUser.getName());
        users.put(newUser.getId(), newUser);
        return newUser;
    }

    private Long generateId() {
        long maxId = users.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0L);
        return ++maxId;
    }

}
