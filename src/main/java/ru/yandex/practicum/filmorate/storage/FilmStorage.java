package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.Collection;

public interface FilmStorage {
    Collection<Film> getFilms();

    Film getFilm(long id);

    boolean contains(long id);

    void saveOrUpdate(Film film);
}
