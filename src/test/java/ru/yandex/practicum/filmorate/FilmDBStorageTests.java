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
import ru.yandex.practicum.filmorate.storage.FilmDBStorage;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;

import java.time.LocalDate;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import({
        FilmDBStorage.class,
        FilmRowMapper.class
})
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDBStorageTests {

    private final FilmDBStorage storage;
    private final JdbcTemplate template;

    @BeforeEach
    public void beforeEach() {
        template.update("DELETE FROM films");
        template.update("ALTER TABLE films ALTER COLUMN id RESTART WITH 1");

        template.update("INSERT INTO films(name, description, release_date, duration) " +
                "VALUES('Film 1', 'Lorem ipsum', '1999-01-01', 120)");
        template.update("INSERT INTO films(name, description, release_date, duration) " +
                "VALUES('Film 2', 'Lorem ipsum', '1999-01-01', 120)");
    }

    @Test
    public void shouldGetFilmById() {
        assertThat(storage.getFilm(1L))
                .isPresent()
                .hasValueSatisfying(film -> {
                    assertThat(film)
                            .hasFieldOrPropertyWithValue("id", 1L);

                    assertThat(film)
                            .hasFieldOrPropertyWithValue("name", "Film 1");

                    assertThat(film)
                            .hasFieldOrPropertyWithValue("description", "Lorem ipsum");

                    assertThat(film)
                            .hasFieldOrPropertyWithValue(
                                    "releaseDate",
                                    LocalDate.of(1999, 1, 1)
                            );

                    assertThat(film)
                            .hasFieldOrPropertyWithValue("duration", 120);
                });
    }

    @Test
    public void shouldNotGetFilmById() {
        assertThat(storage.getFilm(3L)).isEmpty();
    }

    @Test
    public void shouldGetAllFilms() {
        Collection<Film> films = storage.getFilms();

        assertThat(films)
                .hasSize(2)
                .extracting(Film::getId)
                .containsExactlyInAnyOrder(1L, 2L);

        assertThat(films)
                .extracting(Film::getName)
                .containsExactlyInAnyOrder(
                        "Film 1",
                        "Film 2"
                );
    }

    @Test
    public void shouldSaveFilm() {
        Film film = new Film();

        film.setName("New Film");
        film.setDescription("New Description");
        film.setReleaseDate(LocalDate.of(2000, 2, 3));
        film.setDuration(150);

        Film savedFilm = storage.save(film);

        assertThat(savedFilm.getId()).isNotNull();

        assertThat(storage.getFilm(savedFilm.getId()))
                .isPresent()
                .hasValueSatisfying(saved -> {
                    assertThat(saved.getName())
                            .isEqualTo("New Film");

                    assertThat(saved.getDescription())
                            .isEqualTo("New Description");

                    assertThat(saved.getReleaseDate())
                            .isEqualTo(LocalDate.of(2000, 2, 3));

                    assertThat(saved.getDuration())
                            .isEqualTo(150);
                });
    }

    @Test
    public void shouldUpdateFilm() {
        Film film = storage.getFilm(1L).orElseThrow();

        film.setName("Updated Film");
        film.setDescription("Updated Description");
        film.setReleaseDate(LocalDate.of(2010, 10, 10));
        film.setDuration(180);

        storage.update(film);

        assertThat(storage.getFilm(1L))
                .isPresent()
                .hasValueSatisfying(updated -> {
                    assertThat(updated.getName())
                            .isEqualTo("Updated Film");

                    assertThat(updated.getDescription())
                            .isEqualTo("Updated Description");

                    assertThat(updated.getReleaseDate())
                            .isEqualTo(LocalDate.of(2010, 10, 10));

                    assertThat(updated.getDuration())
                            .isEqualTo(180);
                });
    }

}
