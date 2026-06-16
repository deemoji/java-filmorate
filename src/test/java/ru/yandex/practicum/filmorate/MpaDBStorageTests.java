package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaDBStorage;
import ru.yandex.practicum.filmorate.storage.mappers.MpaRowMapper;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import({
        MpaDBStorage.class,
        MpaRowMapper.class
})
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MpaDBStorageTests {

    private final MpaDBStorage storage;

    @Test
    public void shouldGetAllMpa() {
        Collection<Mpa> mpaList = storage.getAll();

        assertThat(mpaList)
                .hasSize(5)
                .extracting(Mpa::getId)
                .containsExactlyInAnyOrder(
                        1L,
                        2L,
                        3L,
                        4L,
                        5L
                );

        assertThat(mpaList)
                .extracting(Mpa::getName)
                .containsExactlyInAnyOrder(
                        "G",
                        "PG",
                        "PG-13",
                        "R",
                        "NC-17"
                );
    }

    @Test
    public void shouldGetMpaById() {
        assertThat(storage.getMpa(1L))
                .isPresent()
                .hasValueSatisfying(mpa -> {

                    assertThat(mpa.getId())
                            .isEqualTo(1L);

                    assertThat(mpa.getName())
                            .isEqualTo("G");
                });
    }

    @Test
    public void shouldNotGetMpaById() {
        assertThat(storage.getMpa(100L))
                .isEmpty();
    }
}
