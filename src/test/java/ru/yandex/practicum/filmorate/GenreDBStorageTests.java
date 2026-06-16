package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreDBStorage;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import({
        GenreDBStorage.class,
        GenreRowMapper.class
})
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreDBStorageTests {

    private final GenreDBStorage storage;
    private final JdbcTemplate template;

    @BeforeEach
    public void beforeEach() {
        template.update("DELETE FROM films_genres");
        template.update("DELETE FROM films");

        template.update("ALTER TABLE films ALTER COLUMN id RESTART WITH 1");

        template.update("""
                INSERT INTO films(name, description, release_date, duration)
                VALUES(
                    'Film 1',
                    'Description',
                    '2000-01-01',
                    120
                )
                """);
    }

    @Test
    public void shouldGetAllGenres() {
        Collection<Genre> genres = storage.getAll();

        assertThat(genres)
                .hasSize(6)
                .extracting(Genre::getId)
                .containsExactlyInAnyOrder(1L, 2L, 3L, 4L, 5L, 6L);

        assertThat(genres)
                .extracting(Genre::getName)
                .containsExactlyInAnyOrder(
                        "Комедия",
                        "Драма",
                        "Боевик",
                        "Мультфильм",
                        "Триллер",
                        "Документальный"
                );
    }

    @Test
    public void shouldGetGenreById() {
        assertThat(storage.getGenre(1L))
                .isPresent()
                .hasValueSatisfying(genre -> {

                    assertThat(genre.getId())
                            .isEqualTo(1L);

                    assertThat(genre.getName())
                            .isEqualTo("Комедия");
                });
    }

    @Test
    public void shouldNotGetGenreById() {
        assertThat(storage.getGenre(10L))
                .isEmpty();
    }

    @Test
    public void shouldSaveFilmGenre() {
        storage.saveFilmGenre(1L, 2L);

        Integer count = template.queryForObject(
                """
                        SELECT COUNT(*)
                        FROM films_genres
                        WHERE film_id = ?
                          AND genre_id = ?
                        """,
                Integer.class,
                1L,
                2L
        );

        assertThat(count).isEqualTo(1);
    }

    @Test
    public void shouldGetFilmGenres() {
        storage.saveFilmGenre(1L, 1L);
        storage.saveFilmGenre(1L, 3L);

        Collection<Genre> genres =
                storage.getFilmGenres(1L);

        assertThat(genres)
                .hasSize(2)
                .extracting(Genre::getId)
                .containsExactlyInAnyOrder(
                        1L,
                        3L
                );

        assertThat(genres)
                .extracting(Genre::getName)
                .containsExactlyInAnyOrder(
                        "Комедия",
                        "Мультфильм"
                );
    }
}
