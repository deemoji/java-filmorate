package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
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
    public Collection<FilmDto> getFilms() {
        return service.getAllFilms();
    }

    @GetMapping("/{id}")
    public FilmDto getFilm(@PathVariable long id) {
        return service.getFilm(id);
    }

    @PostMapping
    public FilmDto createFilm(@Valid @RequestBody NewFilmRequest request) {
        log.info("Создание фильма: {}", request.getName());
        return service.createFilm(request);
    }

    @PutMapping
    public FilmDto updateFilm(@Valid @RequestBody UpdateFilmRequest request) {
        log.info("Обновление фильма: {}", request.getId());
        return service.updateFilm(request);
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
    public Collection<FilmDto> getPopularFilms(@RequestParam(required = false, defaultValue = "0") int count) {
        return service.getTopRatedFilms(count);
    }
}
