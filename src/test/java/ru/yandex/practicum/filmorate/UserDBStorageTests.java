package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDBStorage;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;

import java.time.LocalDate;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import({
        UserDBStorage.class,
        UserRowMapper.class
})
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDBStorageTests {

    private final UserDBStorage storage;
    private final JdbcTemplate template;

    @BeforeEach
    public void beforeEach() {
        template.update("DELETE FROM users");
        template.update("ALTER TABLE users ALTER COLUMN id RESTART WITH 1");

        template.update("INSERT INTO users(email, login, name, birthday) " +
                "VALUES('dima@yandex.ru', 'dimar', 'Dima M' ,'1999-09-19')");
        template.update("INSERT INTO users(email, login, name, birthday) " +
                "VALUES('dimart@yandex.ru', 'dimart', 'Dima M' ,'1999-09-19')");
    }

    @Test
    public void shouldFindUserById() {
        assertThat(storage.getUser(1L))
                .isPresent()
                .hasValueSatisfying(user -> {
                    assertThat(user).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(user).hasFieldOrPropertyWithValue("email", "dima@yandex.ru");
                    assertThat(user).hasFieldOrPropertyWithValue("login", "dimar");
                    assertThat(user).hasFieldOrPropertyWithValue("name", "Dima M");
                    assertThat(user).hasFieldOrPropertyWithValue(
                            "birthday",
                            LocalDate.of(1999, 9, 19)
                    );
                });
    }

    @Test
    public void shouldNotFindUserById() {
        assertThat(storage.getUser(3L)).isEmpty();
    }

    @Test
    public void shouldGetAllUsers() {
        Collection<User> users = storage.getUsers();

        assertThat(users)
                .hasSize(2)
                .extracting(User::getId)
                .containsExactlyInAnyOrder(1L, 2L);

        assertThat(users)
                .extracting(User::getEmail)
                .containsExactlyInAnyOrder(
                        "dima@yandex.ru",
                        "dimart@yandex.ru"
                );
    }

    @Test
    public void shouldSaveUser() {
        User user = new User();
        user.setEmail("new@yandex.ru");
        user.setLogin("newlogin");
        user.setName("New User");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User savedUser = storage.save(user);

        assertThat(savedUser.getId()).isNotNull();

        assertThat(storage.getUser(savedUser.getId()))
                .isPresent()
                .hasValueSatisfying(saved -> {
                    assertThat(saved.getEmail()).isEqualTo("new@yandex.ru");
                    assertThat(saved.getLogin()).isEqualTo("newlogin");
                    assertThat(saved.getName()).isEqualTo("New User");
                    assertThat(saved.getBirthday())
                            .isEqualTo(LocalDate.of(2000, 1, 1));
                });
    }

    @Test
    public void shouldUpdateUser() {
        User user = storage.getUser(1L).orElseThrow();

        user.setEmail("updated@yandex.ru");
        user.setLogin("updatedLogin");
        user.setName("Updated Name");
        user.setBirthday(LocalDate.of(2001, 2, 3));

        storage.update(user);

        assertThat(storage.getUser(1L))
                .isPresent()
                .hasValueSatisfying(updated -> {
                    assertThat(updated.getEmail())
                            .isEqualTo("updated@yandex.ru");
                    assertThat(updated.getLogin())
                            .isEqualTo("updatedLogin");
                    assertThat(updated.getName())
                            .isEqualTo("Updated Name");
                    assertThat(updated.getBirthday())
                            .isEqualTo(LocalDate.of(2001, 2, 3));
                });
    }

}
