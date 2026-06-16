package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
public class UpdateUserRequest {

    @EqualsAndHashCode.Include
    @NotNull
    private Long id;

    @Email(message = "Электронная почта должна соответствовать формату")
    private String email;
    private String login;
    private String name;
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;

    @AssertTrue(message = "Логин не должен содержать пробелы")
    public boolean isLoginValid() {
        if (login == null) return true;
        return !login.contains(" ");
    }
}
