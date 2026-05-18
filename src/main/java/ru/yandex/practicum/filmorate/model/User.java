package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class User {

    @EqualsAndHashCode.Include
    private Long id;

    @Email(message = "Электронная почта должна соответствовать формату")
    private String email;

    @AssertTrue(message = "Логин не должен содержать пробелы")
    public boolean isLoginValid() {
        return login != null && !login.contains(" ");
    }

    @NotBlank(message = "Логин обязателен")
    private String login;

    private String name;

    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
}
