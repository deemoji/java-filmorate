package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

@Data
public class NewFilmRequest implements FilmRequest {
    @NotBlank(message = "Название фильма обязательно")
    private String name;

    @Size(max = 200, message = "Длина описания не может быть больше 200")
    private String description;
    @NotNull(message = "Дата выхода обязательна")
    @PastOrPresent(message = "Дата выхода не может быть в будущем")
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private Integer duration;
    private Mpa mpa;
    private Collection<Genre> genres;

    @AssertTrue(message = "Дата выхода не может быть раньше 28.12.1895")
    public boolean isReleaseDateValid() {
        return releaseDate != null &&
                !releaseDate.isBefore(LocalDate.of(1895, 12, 28));
    }

    @Override
    public Optional<Mpa> getMpa() {
        return Optional.ofNullable(mpa);
    }

    @Override
    public Optional<Collection<Genre>> getGenres() {
        return Optional.ofNullable(genres);
    }
}
