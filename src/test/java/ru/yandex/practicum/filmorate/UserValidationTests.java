package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserValidationTests {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void beforeAll() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void afterAll() {
        factory.close();
    }

    @Test
    public void shouldPassWhenEmailIsValid() {
        NewUserRequest user = new NewUserRequest();
        user.setEmail("abc@email.com");

        Set<ConstraintViolation<NewUserRequest>> violations = validator.validateProperty(user, "email");
        assertTrue(violations.isEmpty(), "Ожидается отсутствие ошибок валидации поля");
    }

    @Test
    public void shouldFailValidationWhenEmailIsBlank() {
        NewUserRequest user = new NewUserRequest();
        user.setEmail(" ");

        Set<ConstraintViolation<NewUserRequest>> violations = validator.validateProperty(user, "email");
        assertFalse(violations.isEmpty(), "Ожидается ошибка валидации");
        String message = violations.iterator().next().getMessage();

        assertEquals(
                "Электронная почта должна соответствовать формату",
                message,
                "Тип ошибки не соответствует ожидаемому");
    }

    @Test
    public void shouldFailValidationWhenEmailIsNotCorrect() {
        NewUserRequest user = new NewUserRequest();
        user.setEmail("some-ema?il@");

        Set<ConstraintViolation<NewUserRequest>> violations = validator.validateProperty(user, "email");
        assertFalse(violations.isEmpty(), "Ожидается ошибка валидации");
        String message = violations.iterator().next().getMessage();

        assertEquals(
                "Электронная почта должна соответствовать формату",
                message,
                "Тип ошибки не соответствует ожидаемому");
    }

    @Test
    public void shouldPassWhenLoginIsValid() {
        NewUserRequest user = new NewUserRequest();
        user.setLogin("yuri-gagarin");

        Set<ConstraintViolation<NewUserRequest>> violations = validator.validateProperty(user, "login");
        assertTrue(violations.isEmpty(), "Ожидается отсутствие ошибок валидации поля");
    }

    @Test
    public void shouldFailValidationWhenLoginIsBlank() {
        NewUserRequest user = new NewUserRequest();
        user.setLogin(" ");

        Set<ConstraintViolation<NewUserRequest>> violations = validator.validateProperty(user, "login");
        assertFalse(violations.isEmpty(), "Ожидается ошибка валидации");
        String message = violations.iterator().next().getMessage();

        assertEquals(
                "Логин обязателен",
                message,
                "Тип ошибки не соответствует ожидаемому");
    }

    @Test
    public void shouldFailValidationWhenLoginHasWhitespaces() {
        NewUserRequest user = new NewUserRequest();
        user.setLogin("yuri gagarin");

        Set<ConstraintViolation<NewUserRequest>> propertyViolations = validator.validateProperty(
                user, "login"
        );
        Set<ConstraintViolation<NewUserRequest>> allViolations = validator.validate(user);

        assertTrue(propertyViolations.isEmpty(), "Ожидается отсутствие ошибок валидации поля");
        assertTrue(allViolations.stream().anyMatch(violation ->
                violation.getMessage().equals("Логин не должен содержать пробелы")
        ), "Ожидается ошибка валидации");
    }

    @Test
    public void shouldPassWhenBirthdayIsNow() {
        NewUserRequest user = new NewUserRequest();
        user.setBirthday(LocalDate.now());

        Set<ConstraintViolation<NewUserRequest>> violations = validator.validateProperty(user, "birthday");
        assertTrue(violations.isEmpty(), "Ожидается отсутствие ошибок валидации поля");
    }

    @Test
    public void shouldFailValidationWhenBirthdayIsInFuture() {
        NewUserRequest user = new NewUserRequest();
        user.setBirthday(LocalDate.now().plusDays(1));

        Set<ConstraintViolation<NewUserRequest>> violations = validator.validateProperty(user, "birthday");

        assertTrue(violations.stream().anyMatch(violation ->
                violation.getMessage().equals("Дата рождения не может быть в будущем")
        ), "Ожидается ошибка валидации");
    }
}
