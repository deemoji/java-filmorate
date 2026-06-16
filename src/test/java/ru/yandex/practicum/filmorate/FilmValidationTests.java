package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FilmValidationTests {

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
    public void shouldPassWhenNameIsValid() {
        NewFilmRequest film = new NewFilmRequest();
        film.setName("Memento");

        Set<ConstraintViolation<NewFilmRequest>> violations = validator.validateProperty(film, "name");
        assertTrue(violations.isEmpty(), "Ожидается отсутствие ошибок валидации поля");
    }

    @Test
    public void shouldFailValidationWhenNameIsBlank() {
        NewFilmRequest film = new NewFilmRequest();
        film.setName(" ");

        Set<ConstraintViolation<NewFilmRequest>> violations = validator.validateProperty(film, "name");
        assertFalse(violations.isEmpty(), "Ожидается ошибка валидации");
        String message = violations.iterator().next().getMessage();

        assertEquals(
                "Название фильма обязательно",
                message,
                "Тип ошибки не соответствует ожидаемому");
    }

    @Test
    public void shouldPassWhenDescriptionLengthIs200() throws NoSuchMethodException {
        NewFilmRequest film = new NewFilmRequest();
        film.setDescription("a".repeat(200));

        Set<ConstraintViolation<NewFilmRequest>> violations = validator.validateProperty(film, "description");
        assertTrue(violations.isEmpty(), "Ожидается отсутствие ошибок валидации поля");
    }

    @Test
    public void shouldFailValidationWhenDescriptionLengthIs201() {
        NewFilmRequest film = new NewFilmRequest();
        film.setDescription("a".repeat(202));

        Set<ConstraintViolation<NewFilmRequest>> violations = validator.validateProperty(film, "description");
        assertTrue(violations.stream().anyMatch(violation ->
                violation.getMessage().equals("Длина описания не может быть больше 200")
        ), "Ожидается ошибка валидации");

    }

    @Test
    public void shouldPassWhenReleaseDateIs28_12_1895() {
        NewFilmRequest film = new NewFilmRequest();
        film.setReleaseDate(LocalDate.of(1895, 12, 28));

        Set<ConstraintViolation<NewFilmRequest>> propertyViolations = validator.validateProperty(
                film, "releaseDate"
        );
        Set<ConstraintViolation<NewFilmRequest>> allViolations = validator.validate(film);
        boolean isValid = allViolations.stream().noneMatch(violation ->
                violation.getMessage().equals("Дата выхода не может быть раньше 28.12.1895")
        );

        assertTrue(propertyViolations.isEmpty(), "Ожидается отсутствие ошибок валидации поля");
        assertTrue(isValid, "Фильм должен проходить валидацию");
    }

    @Test
    public void shouldFailValidationWhenReleaseDateIs27_12_1895() {
        NewFilmRequest film = new NewFilmRequest();
        film.setReleaseDate(LocalDate.of(1895, 12, 27));

        Set<ConstraintViolation<NewFilmRequest>> propertyViolations = validator.validateProperty(
                film, "releaseDate"
        );
        Set<ConstraintViolation<NewFilmRequest>> allViolations = validator.validate(film);

        assertTrue(propertyViolations.isEmpty(), "Ожидается отсутствие ошибок валидации поля");
        assertTrue(allViolations.stream().anyMatch(violation ->
                violation.getMessage().equals("Дата выхода не может быть раньше 28.12.1895")
        ), "Ожидается ошибка валидации");
    }

    @Test
    public void shouldPassWhenReleaseDateIsNow() {
        NewFilmRequest film = new NewFilmRequest();
        film.setReleaseDate(LocalDate.now());

        Set<ConstraintViolation<NewFilmRequest>> propertyViolations = validator.validateProperty(
                film, "releaseDate"
        );
        Set<ConstraintViolation<NewFilmRequest>> allViolations = validator.validate(film);
        boolean isValid = allViolations.stream().noneMatch(violation ->
                violation.getMessage().equals("Дата выхода не может быть раньше 28.12.1895")
        );

        assertTrue(propertyViolations.isEmpty(), "Ожидается отсутствие ошибок валидации поля");
        assertTrue(isValid, "Фильм должен проходить валидацию");
    }

    @Test
    public void shouldFailValidationWhenReleaseDateIsInFuture() {
        NewFilmRequest film = new NewFilmRequest();
        film.setReleaseDate(LocalDate.now().plusDays(1));

        Set<ConstraintViolation<NewFilmRequest>> violations = validator.validateProperty(film, "releaseDate");

        assertTrue(violations.stream().anyMatch(violation ->
                violation.getMessage().equals("Дата выхода не может быть в будущем")
        ), "Ожидается ошибка валидации");
    }

    @Test
    public void shouldPassWhenDurationIsPositive() {
        NewFilmRequest film = new NewFilmRequest();
        film.setDuration(1);

        Set<ConstraintViolation<NewFilmRequest>> violations = validator.validateProperty(film, "duration");
        assertTrue(violations.isEmpty(), "Ожидается отсутствие ошибок валидации поля");
    }

    @Test
    public void shouldFailValidationWhenDurationIsNotPositive() {
        NewFilmRequest film = new NewFilmRequest();
        film.setDuration(0);

        Set<ConstraintViolation<NewFilmRequest>> violations = validator.validateProperty(film, "duration");

        assertTrue(violations.stream().anyMatch(violation ->
                violation.getMessage().equals("Продолжительность фильма должна быть положительным числом")
        ), "Ожидается ошибка валидации");
    }

}
