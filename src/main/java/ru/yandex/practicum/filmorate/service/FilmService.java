package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.FilmRequest;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.*;
import ru.yandex.practicum.filmorate.util.Constants;

import java.util.Collection;
import java.util.Optional;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikeStorage likeStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;

    @Autowired
    public FilmService(@Qualifier("filmDBStorage") FilmStorage filmStorage,
                       @Qualifier("userDBStorage") UserStorage userStorage,
                       @Qualifier("likeDBStorage") LikeStorage likeStorage,
                       GenreStorage genreStorage,
                       MpaStorage mpaStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.likeStorage = likeStorage;
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
    }

    public FilmDto createFilm(NewFilmRequest request) {
        validateFilmData(request);

        Film savedFilm = filmStorage.save(FilmMapper.mapToFilm(request));
        request.getGenres().ifPresent(
                genres -> saveFilmGenres(savedFilm.getId(), genres)
        );

        return mapToFilmDto(savedFilm);
    }

    public FilmDto updateFilm(UpdateFilmRequest request) {
        validateFilmData(request);

        Film filmToUpdate = filmStorage.getFilm(request.getId())
                .map(film -> FilmMapper.updateFilmFields(film, request))
                .orElseThrow(() -> new NotFoundException("Фильм c Id " + request.getId() + " не найден"));

        request.getGenres().ifPresent(
                genres -> saveFilmGenres(filmToUpdate.getId(), genres)
        );

        return mapToFilmDto(filmStorage.update(filmToUpdate));
    }

    private void validateFilmData(FilmRequest request) {
        Optional<Collection<Genre>> genresOptional = request.getGenres();
        Optional<Mpa> mpaOptional = request.getMpa();

        if (mpaOptional.isPresent() && isMpaCorrupted(mpaOptional.get())) {
            throw new NotFoundException("Рейтинг с id " + mpaOptional.get().getId() + "не найден");
        }

        if (genresOptional.isPresent() && isGenresCorrupted(genresOptional.get())) {
            throw new NotFoundException("Один из жанров фильма не определен");
        }
    }

    private boolean isMpaCorrupted(Mpa mpa) {
        return mpaStorage.getMpa(mpa.getId()).isEmpty();
    }

    private boolean isGenresCorrupted(Collection<Genre> genres) {
        Collection<Genre> dbGenres = genreStorage.getAll();
        return !dbGenres.containsAll(genres);
    }

    private void saveFilmGenres(long filmId, Collection<Genre> genres) {
        genres.forEach(
                genre -> genreStorage.saveFilmGenre(filmId, genre.getId())
        );
    }

    private FilmDto mapToFilmDto(Film film) {
        Mpa mpa = getFilmMpa(film);
        Collection<Genre> genres = genreStorage.getFilmGenres(film.getId());
        return FilmMapper.mapToFilmDto(film, mpa, genres);
    }

    private Mpa getFilmMpa(Film film) {
        return Optional.ofNullable(film.getMpaId())
                .flatMap(mpaStorage::getMpa)
                .orElse(null);
    }

    public Collection<FilmDto> getAllFilms() {
        return filmStorage.getFilms().stream()
                .map(this::mapToFilmDto)
                .toList();
    }

    public FilmDto getFilm(Long id) {
        Film film = filmStorage.getFilm(id)
                .orElseThrow(() -> new NotFoundException("Фильм c Id " + id + " не найден"));
        return mapToFilmDto(film);
    }

    public void setLike(Long filmId, Long userId) {
        Film film = filmStorage.getFilm(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм c Id " + filmId + " не найден"));
        User user = userStorage.getUser(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь c Id " + userId + " не найден"));

        likeStorage.saveLike(film.getId(), user.getId());
    }

    public void deleteLike(Long filmId, Long userId) {
        Film film = filmStorage.getFilm(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм c Id " + filmId + " не найден"));
        User user = userStorage.getUser(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь c Id " + userId + " не найден"));

        likeStorage.deleteLike(film.getId(), user.getId());
    }

    public Collection<FilmDto> getTopRatedFilms(int count) {
        int limit = count == Constants.TOP_FILMS_DEFAULT_VALUE ? Constants.TOP_RATED_FILMS_COUNT : count;
        return likeStorage.getPopularFilms(limit).stream()
                .map(this::mapToFilmDto)
                .toList();
    }
}
