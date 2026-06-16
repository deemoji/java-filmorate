package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

@Repository
public class FriendshipDBStorage extends BaseRepository<User> implements FriendshipStorage {

    private static final String INSERT_QUERY = "INSERT INTO friendships(sender_id, receiver_id) VALUES(?, ?)";
    private static final String SELECT_USER_FRIENDS_QUERY = "SELECT * FROM users WHERE id " +
            "IN (SELECT receiver_id FROM friendships WHERE sender_id = ?)";
    private static final String SELECT_USER_COMMON_FRIENDS_QUERY = "SELECT u.* FROM users AS u JOIN friendships AS f1 " +
            "ON u.id = f1.receiver_id JOIN friendships f2 ON u.id = f2.receiver_id WHERE f1.sender_id = ? AND " +
            "f2.sender_id = ?";
    private static final String DELETE_FRIENDS_QUERY = "DELETE FROM friendships WHERE sender_id = ? " +
            "AND receiver_id = ?";

    public FriendshipDBStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public void saveFriends(Long userIdOne, Long userIdTwo) {
        insertWithNoResult(INSERT_QUERY, userIdOne, userIdTwo);
    }

    @Override
    public Collection<User> getUserFriends(Long userId) {
        return findMany(SELECT_USER_FRIENDS_QUERY, userId);
    }

    @Override
    public Collection<User> getCommonFriends(Long userIdOne, Long userIdTwo) {
        return findMany(SELECT_USER_COMMON_FRIENDS_QUERY, userIdOne, userIdTwo);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        delete(DELETE_FRIENDS_QUERY, userId, friendId);
    }
}
