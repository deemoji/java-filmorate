package ru.yandex.practicum.filmorate.dto;

import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.Optional;

public interface FilmRequest {

    Optional<Mpa> getMpa();

    Optional<Collection<Genre>> getGenres();

}
