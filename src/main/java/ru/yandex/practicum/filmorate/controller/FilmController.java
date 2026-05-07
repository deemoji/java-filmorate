package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.util.Constants;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getFilms() {
        return films.values();
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        return saveNewFilm(film);
    }

    private Film saveNewFilm(Film film) {
        log.info("Создание фильма: {}", film.getName());

        film.setId(generateId());
        films.put(film.getId(), film);
        return film;
    }

    private Long generateId() {
        long maxId = films.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(Constants.ID_GENERATOR_START_INDEX);
        return ++maxId;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        return updateExistedFilm(film);
    }

    private Film updateExistedFilm(Film film) {
        log.info("Обновление фильма id={}", film.getId());

        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("Фильм c Id " + film.getId() + " не найден");
        }
        films.put(film.getId(), film);
        return film;
    }

}
