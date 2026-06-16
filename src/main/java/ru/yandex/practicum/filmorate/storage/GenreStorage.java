package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Optional;

public interface GenreStorage {
    Collection<Genre> getAll();

    void saveFilmGenre(long filmId, long genreId);

    Collection<Genre> getFilmGenres(long filmId);

    Optional<Genre> getGenre(long id);
}
