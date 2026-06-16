package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

@Repository
public class LikeDBStorage extends BaseRepository<Film> implements LikeStorage {

    private static final String INSERT_QUERY = "INSERT INTO liked_films(film_id, user_id) VALUES(?, ?)";
    private static final String DELETE_FRIENDS_QUERY = "DELETE FROM liked_films WHERE film_id = ? " +
            "AND user_id = ?";
    private static final String SELECT_POPULAR_FILMS_QUERY = "SELECT f.* FROM liked_films AS lf " +
            "JOIN films AS f ON lf.film_id = f.id GROUP BY lf.film_id ORDER BY COUNT(lf.user_id) DESC LIMIT ?";

    public LikeDBStorage(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public void saveLike(Long filmId, Long userId) {
        insertWithNoResult(INSERT_QUERY, filmId, userId);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        delete(DELETE_FRIENDS_QUERY, filmId, userId);
    }

    @Override
    public Collection<Film> getPopularFilms(int count) {
        return findMany(SELECT_POPULAR_FILMS_QUERY, count);
    }
}
