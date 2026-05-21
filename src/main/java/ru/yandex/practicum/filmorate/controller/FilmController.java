package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService service;

    @Autowired
    public FilmController(FilmService service) {
        this.service = service;
    }

    @GetMapping
    public Collection<Film> getFilms() {
        return service.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable long id) {
        return service.getFilm(id);
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("Создание фильма: {}", film.getName());
        return service.createFilm(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("Обновление фильма: {}", film.getName());
        return service.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void setLikeToFilm(@PathVariable long id, @PathVariable long userId) {
        log.info("Пользователь {} ставит лайк фильму: {}", userId, id);
        service.setLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteUserLikeFromFilm(@PathVariable long id, @PathVariable long userId) {
        log.info("Пользователь {} убирает лайк с фильма: {}", userId, id);
        service.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(@RequestParam(required = false, defaultValue = "0") int count) {
        return service.getTopRatedFilms(count);
    }
}
