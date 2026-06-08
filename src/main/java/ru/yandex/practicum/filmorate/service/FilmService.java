package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.util.Constants;

import java.util.Collection;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikeStorage likeStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage, LikeStorage likeStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.likeStorage = likeStorage;
    }

    public Film createFilm(Film film) {
        film.setId(generateId());
        filmStorage.saveOrUpdate(film);
        return film;
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getFilms();
    }

    public Film getFilm(Long id) {
        if (filmStorage.contains(id)) {
            return filmStorage.getFilm(id);
        }
        throw new NotFoundException("Фильм c Id " + id + " не найден");
    }

    public Film updateFilm(Film film) {
        if (filmStorage.contains(film.getId())) {
            filmStorage.saveOrUpdate(film);
            return film;
        }
        throw new NotFoundException("Фильм c Id " + film.getId() + " не найден");
    }

    public void setLike(Long filmId, Long userId) {
        if (filmStorage.contains(filmId)) {
            if (userStorage.contains(userId)) {
                likeStorage.saveLike(filmId, userId);
                return;
            }
            throw new NotFoundException("Пользователь c Id " + userId + " не найден");
        }
        throw new NotFoundException("Фильм c Id " + filmId + " не найден");
    }

    public void deleteLike(Long filmId, Long userId) {
        if (filmStorage.contains(filmId)) {
            if (userStorage.contains(userId)) {
                likeStorage.deleteLike(filmId, userId);
                return;
            }
            throw new NotFoundException("Пользователь c Id " + userId + " не найден");
        }
        throw new NotFoundException("Фильм c Id " + filmId + " не найден");
    }

    public Collection<Film> getTopRatedFilms(int count) {
        int limit = count == Constants.TOP_FILMS_DEFAULT_VALUE ? Constants.TOP_RATED_FILMS_COUNT : count;
        return likeStorage.getPopularFilms(limit).stream()
                .map(filmStorage::getFilm)
                .toList();
    }

    private Long generateId() {
        long maxId = filmStorage.getFilms().stream()
                .mapToLong(Film::getId)
                .max()
                .orElse(Constants.ID_GENERATOR_START_INDEX);
        return ++maxId;
    }
}
