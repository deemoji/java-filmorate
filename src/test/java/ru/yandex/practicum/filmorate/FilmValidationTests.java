package ru.yandex.practicum.filmorate;

import jakarta.validation.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.util.Set;
import ru.yandex.practicum.filmorate.model.Film;

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
        Film film = new Film();
        film.setName("Memento");

        Set<ConstraintViolation<Film>> violations = validator.validateProperty(film, "name");
        assertTrue(violations.isEmpty(), "Ожидается отсутствие ошибок валидации поля");
    }

    @Test
    public void shouldFailValidationWhenNameIsBlank() {
        Film film = new Film();
        film.setName(" ");

        Set<ConstraintViolation<Film>> violations = validator.validateProperty(film, "name");
        assertFalse(violations.isEmpty(), "Ожидается ошибка валидации");
        String message = violations.iterator().next().getMessage();

        assertEquals(
                "Название фильма обязательно",
                message,
                "Тип ошибки не соответствует ожидаемому");
    }

    @Test
    public void shouldPassWhenDescriptionLengthIs200() throws NoSuchMethodException {
        Film film = new Film();
        film.setDescription("a".repeat(200));

        Set<ConstraintViolation<Film>> violations = validator.validateProperty(film, "description");
        assertTrue(violations.isEmpty(), "Ожидается отсутствие ошибок валидации поля");
    }

    @Test
    public void shouldFailValidationWhenDescriptionLengthIs201() {
        Film film = new Film();
        film.setDescription("a".repeat(202));

        Set<ConstraintViolation<Film>> violations = validator.validateProperty(film, "description");
        assertTrue(violations.stream().anyMatch(violation ->
                violation.getMessage().equals("Длина описания не может быть больше 200")
        ), "Ожидается ошибка валидации");

    }

    @Test
    public void shouldPassWhenReleaseDateIs28_12_1895() {
        Film film = new Film();
        film.setReleaseDate(LocalDate.of(1895, 12, 28));

        Set<ConstraintViolation<Film>> propertyViolations = validator.validateProperty(film, "releaseDate");
        Set<ConstraintViolation<Film>> allViolations = validator.validate(film);
        boolean isValid = allViolations.stream().noneMatch(violation ->
                violation.getMessage().equals("Дата выхода не может быть раньше 28.12.1895")
        );

        assertTrue(propertyViolations.isEmpty(), "Ожидается отсутствие ошибок валидации поля");
        assertTrue(isValid, "Фильм должен проходить валидацию");
    }

    @Test
    public void shouldFailValidationWhenReleaseDateIs27_12_1895() {
        Film film = new Film();
        film.setReleaseDate(LocalDate.of(1895, 12, 27));

        Set<ConstraintViolation<Film>> propertyViolations = validator.validateProperty(film, "releaseDate");
        Set<ConstraintViolation<Film>> allViolations = validator.validate(film);

        assertTrue(propertyViolations.isEmpty(), "Ожидается отсутствие ошибок валидации поля");
        assertTrue(allViolations.stream().anyMatch(violation ->
                violation.getMessage().equals("Дата выхода не может быть раньше 28.12.1895")
        ), "Ожидается ошибка валидации");
    }

    @Test
    public void shouldPassWhenReleaseDateIsNow() {
        Film film = new Film();
        film.setReleaseDate(LocalDate.now());

        Set<ConstraintViolation<Film>> propertyViolations = validator.validateProperty(film, "releaseDate");
        Set<ConstraintViolation<Film>> allViolations = validator.validate(film);
        boolean isValid = allViolations.stream().noneMatch(violation ->
                violation.getMessage().equals("Дата выхода не может быть раньше 28.12.1895")
        );

        assertTrue(propertyViolations.isEmpty(), "Ожидается отсутствие ошибок валидации поля");
        assertTrue(isValid, "Фильм должен проходить валидацию");
    }

    @Test
    public void shouldFailValidationWhenReleaseDateIsInFuture() {
        Film film = new Film();
        film.setReleaseDate(LocalDate.now().plusDays(1));

        Set<ConstraintViolation<Film>> violations = validator.validateProperty(film, "releaseDate");

        assertTrue(violations.stream().anyMatch(violation ->
                violation.getMessage().equals("Дата выхода не может быть в будущем")
        ), "Ожидается ошибка валидации");
    }

    @Test
    public void shouldPassWhenDurationIsPositive() {
        Film film = new Film();
        film.setDuration(1);

        Set<ConstraintViolation<Film>> violations = validator.validateProperty(film, "duration");
        assertTrue(violations.isEmpty(), "Ожидается отсутствие ошибок валидации поля");
    }

    @Test
    public void shouldFailValidationWhenDurationIsNotPositive() {
        Film film = new Film();
        film.setDuration(0);

        Set<ConstraintViolation<Film>> violations = validator.validateProperty(film, "duration");

        assertTrue(violations.stream().anyMatch(violation ->
                violation.getMessage().equals("Продолжительность фильма должна быть положительным числом")
        ), "Ожидается ошибка валидации");
    }

}
