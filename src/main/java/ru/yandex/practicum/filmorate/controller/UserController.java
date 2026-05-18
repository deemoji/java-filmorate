package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.util.Constants;
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
        return saveNewUser(user);
    }

    private User saveNewUser(User user) {
        log.info("Создание пользователя: {}", user.getName());

        user.setId(generateId());
        user.setName(user.getName() == null ? user.getLogin() : user.getName());
        users.put(user.getId(), user);
        return user;
    }

    private Long generateId() {
        long maxId = users.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(Constants.ID_GENERATOR_START_INDEX);
        return ++maxId;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        return updateExistedUser(user);
    }

    private User updateExistedUser(User user) {
        log.info("Обновление пользователя: {}", user.getName());

        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("Пользователь c Id " + user.getId() + " не найден");
        }
        user.setName(user.getName() == null ? user.getLogin() : user.getName());
        users.put(user.getId(), user);
        return user;
    }
}
