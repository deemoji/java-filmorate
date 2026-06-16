package ru.yandex.practicum.filmorate.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.Collection;

@Data
public class FilmDto {
    @EqualsAndHashCode.Include
    private Long id;

    private String name;

    private String description;

    private LocalDate releaseDate;

    private Integer duration;

    private Mpa mpa;

    private Collection<Genre> genres;
}
