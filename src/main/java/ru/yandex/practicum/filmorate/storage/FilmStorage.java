package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.Collection;

public interface FilmStorage {
    Collection<Film> getFilms();
    void add(Film film);
    void update(Film film);
    void delete(Long id);
}
