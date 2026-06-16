package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.LikeDBStorage;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import({
        LikeDBStorage.class,
        FilmRowMapper.class
})
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class LikeDBStorageTests {

    private final LikeDBStorage storage;
    private final JdbcTemplate template;

    @BeforeEach
    public void beforeEach() {
        template.update("DELETE FROM liked_films");
        template.update("DELETE FROM films");
        template.update("DELETE FROM users");

        template.update("ALTER TABLE films ALTER COLUMN id RESTART WITH 1");
        template.update("ALTER TABLE users ALTER COLUMN id RESTART WITH 1");

        template.update("""
                INSERT INTO users(email, login, name, birthday)
                VALUES('user1@yandex.ru', 'user1', 'User One', '1990-01-01')
                """);

        template.update("""
                INSERT INTO users(email, login, name, birthday)
                VALUES('user2@yandex.ru', 'user2', 'User Two', '1991-01-01')
                """);

        template.update("""
                INSERT INTO users(email, login, name, birthday)
                VALUES('user3@yandex.ru', 'user3', 'User Three', '1992-01-01')
                """);

        template.update("""
                INSERT INTO films(name, description, release_date, duration)
                VALUES('Film 1', 'Description', '2000-01-01', 120)
                """);

        template.update("""
                INSERT INTO films(name, description, release_date, duration)
                VALUES('Film 2', 'Description', '2001-01-01', 130)
                """);

        template.update("""
                INSERT INTO films(name, description, release_date, duration)
                VALUES('Film 3', 'Description', '2002-01-01', 140)
                """);
    }

    @Test
    public void shouldSaveLike() {
        storage.saveLike(1L, 1L);

        Integer count = template.queryForObject(
                """
                        SELECT COUNT(*)
                        FROM liked_films
                        WHERE film_id = 1
                        AND user_id = 1
                        """,
                Integer.class
        );

        assertThat(count).isEqualTo(1);
    }

    @Test
    public void shouldDeleteLike() {
        storage.saveLike(1L, 1L);

        storage.deleteLike(1L, 1L);

        Integer count = template.queryForObject(
                """
                        SELECT COUNT(*)
                        FROM liked_films
                        WHERE film_id = 1
                        AND user_id = 1
                        """,
                Integer.class
        );

        assertThat(count).isZero();
    }

    @Test
    public void shouldGetPopularFilms() {
        storage.saveLike(1L, 1L);
        storage.saveLike(1L, 2L);
        storage.saveLike(1L, 3L);

        storage.saveLike(2L, 1L);
        storage.saveLike(2L, 2L);

        storage.saveLike(3L, 1L);

        Collection<Film> popularFilms = storage.getPopularFilms(2);

        assertThat(popularFilms)
                .hasSize(2)
                .extracting(Film::getId)
                .containsExactly(1L, 2L);

        assertThat(popularFilms)
                .extracting(Film::getName)
                .containsExactly("Film 1", "Film 2");
    }

}
