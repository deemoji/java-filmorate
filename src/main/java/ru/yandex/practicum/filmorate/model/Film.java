package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Data
public class Film {

    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank(message = "Название фильма обязательно")
    private String name;

    @Size(max = 200, message = "Длина описания не может быть больше 200")
    private String description;

    @AssertTrue(message = "Дата выхода не может быть раньше 28.12.1895")
    public boolean isReleaseDateValid() {
        return releaseDate != null &&
                !releaseDate.isBefore(LocalDate.of(1895, 12, 28));
    }

    @NotNull(message = "Дата выхода обязательна")
    @PastOrPresent(message = "Дата выхода не может быть в будущем")
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private int duration;
}
