package ru.yandex.practicum.filmorate;

import jakarta.validation.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.util.Set;
import ru.yandex.practicum.filmorate.model.User;

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
        User user = new User();
        user.setEmail("abc@email.com");

        Set<ConstraintViolation<User>> violations = validator.validateProperty(user, "email");
        assertTrue(violations.isEmpty(), "Ожидается отсутствие ошибок валидации поля");
    }

    @Test
    public void shouldFailValidationWhenEmailIsBlank() {
        User user = new User();
        user.setEmail(" ");

        Set<ConstraintViolation<User>> violations = validator.validateProperty(user, "email");
        assertFalse(violations.isEmpty(), "Ожидается ошибка валидации");
        String message = violations.iterator().next().getMessage();

        assertEquals(
                "Электронная почта обязательна",
                message,
                "Тип ошибки не соответствует ожидаемому");
    }

    @Test
    public void shouldFailValidationWhenEmailIsNotCorrect() {
        User user = new User();
        user.setEmail("some-ema?il@");

        Set<ConstraintViolation<User>> violations = validator.validateProperty(user, "email");
        assertFalse(violations.isEmpty(), "Ожидается ошибка валидации");
        String message = violations.iterator().next().getMessage();

        assertEquals(
                "Электронная почта должна соответствовать формату",
                message,
                "Тип ошибки не соответствует ожидаемому");
    }

    @Test
    public void shouldPassWhenLoginIsValid() {
        User user = new User();
        user.setLogin("yuri-gagarin");

        Set<ConstraintViolation<User>> violations = validator.validateProperty(user, "login");
        assertTrue(violations.isEmpty(), "Ожидается отсутствие ошибок валидации поля");
    }

    @Test
    public void shouldFailValidationWhenLoginIsBlank() {
        User user = new User();
        user.setLogin(" ");

        Set<ConstraintViolation<User>> violations = validator.validateProperty(user, "login");
        assertFalse(violations.isEmpty(), "Ожидается ошибка валидации");
        String message = violations.iterator().next().getMessage();

        assertEquals(
                "Логин обязателен",
                message,
                "Тип ошибки не соответствует ожидаемому");
    }

    @Test
    public void shouldFailValidationWhenLoginHasWhitespaces() {
        User user = new User();
        user.setLogin("yuri gagarin");

        Set<ConstraintViolation<User>> propertyViolations = validator.validateProperty(user, "login");
        Set<ConstraintViolation<User>> allViolations = validator.validate(user);

        assertTrue(propertyViolations.isEmpty(), "Ожидается отсутствие ошибок валидации поля");
        assertTrue(allViolations.stream().anyMatch(violation ->
                violation.getMessage().equals("Логин не должен содержать пробелы")
        ), "Ожидается ошибка валидации");
    }

    @Test
    public void shouldPassWhenBirthdayIsNow() {
        User user = new User();
        user.setBirthday(LocalDate.now());

        Set<ConstraintViolation<User>> violations = validator.validateProperty(user, "birthday");
        assertTrue(violations.isEmpty(), "Ожидается отсутствие ошибок валидации поля");
    }

    @Test
    public void shouldFailValidationWhenBirthdayIsInFuture() {
        User user = new User();
        user.setBirthday(LocalDate.now().plusDays(1));

        Set<ConstraintViolation<User>> violations = validator.validateProperty(user, "birthday");

        assertTrue(violations.stream().anyMatch(violation ->
                violation.getMessage().equals("Дата рождения не может быть в будущем")
        ), "Ожидается ошибка валидации");
    }
}
