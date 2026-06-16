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
import ru.yandex.practicum.filmorate.storage.FriendshipDBStorage;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;

import java.time.LocalDate;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import({
        FriendshipDBStorage.class,
        UserRowMapper.class
})
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FriendshipDBStorageTests {

    private final FriendshipDBStorage storage;
    private final JdbcTemplate template;

    @BeforeEach
    public void beforeEach() {
        template.update("DELETE FROM friendships");
        template.update("DELETE FROM users");

        template.update("ALTER TABLE users ALTER COLUMN id RESTART WITH 1");

        template.update(
                "INSERT INTO users(email, login, name, birthday) VALUES(?, ?, ?, ?)",
                "user1@yandex.ru",
                "user1",
                "User One",
                LocalDate.of(1990, 1, 1)
        );

        template.update(
                "INSERT INTO users(email, login, name, birthday) VALUES(?, ?, ?, ?)",
                "user2@yandex.ru",
                "user2",
                "User Two",
                LocalDate.of(1991, 2, 2)
        );

        template.update(
                "INSERT INTO users(email, login, name, birthday) VALUES(?, ?, ?, ?)",
                "user3@yandex.ru",
                "user3",
                "User Three",
                LocalDate.of(1992, 3, 3)
        );
    }

    @Test
    public void shouldSaveFriend() {
        storage.saveFriends(1L, 2L);

        Integer count = template.queryForObject(
                """
                        SELECT COUNT(*)
                        FROM friendships
                        WHERE sender_id = 1
                        AND receiver_id = 2
                        """,
                Integer.class
        );

        assertThat(count).isEqualTo(1);
    }

    @Test
    public void shouldGetUserFriends() {
        storage.saveFriends(1L, 2L);
        storage.saveFriends(1L, 3L);

        Collection<User> friends = storage.getUserFriends(1L);

        assertThat(friends)
                .hasSize(2)
                .extracting(User::getId)
                .containsExactlyInAnyOrder(2L, 3L);

        assertThat(friends)
                .extracting(User::getLogin)
                .containsExactlyInAnyOrder("user2", "user3");
    }

    @Test
    public void shouldGetCommonFriends() {
        storage.saveFriends(1L, 2L);
        storage.saveFriends(1L, 3L);

        storage.saveFriends(2L, 3L);

        Collection<User> commonFriends =
                storage.getCommonFriends(1L, 2L);

        assertThat(commonFriends)
                .hasSize(1)
                .extracting(User::getId)
                .containsExactly(3L);

        assertThat(commonFriends)
                .extracting(User::getLogin)
                .containsExactly("user3");
    }

    @Test
    public void shouldDeleteFriend() {
        storage.saveFriends(1L, 2L);

        storage.deleteFriend(1L, 2L);

        Collection<User> friends = storage.getUserFriends(1L);

        assertThat(friends).isEmpty();

        Integer count = template.queryForObject(
                """
                        SELECT COUNT(*)
                        FROM friendships
                        WHERE sender_id = 1
                        AND receiver_id = 2
                        """,
                Integer.class
        );

        assertThat(count).isZero();
    }

}
