package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDate;

@Data
public class NewUserRequest {
    @Email(message = "Электронная почта должна соответствовать формату")
    private String email;
    @NotBlank(message = "Логин обязателен")
    private String login;
    private String name;
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;

    @AssertTrue(message = "Логин не должен содержать пробелы")
    public boolean isLoginValid() {
        return login != null && !login.contains(" ");
    }
}
