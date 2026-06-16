package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Repository
public class GenreDBStorage extends BaseRepository<Genre> implements GenreStorage {

    private static final String FIND_ALL_QUERY = "SELECT * FROM genres";

    private static final String INSERT_QUERY = "INSERT INTO films_genres VALUES(?, ?)";

    private static final String FIND_FILM_GENRES_QUERY = "SELECT * FROM genres WHERE id IN " +
            "(SELECT genre_id FROM films_genres WHERE film_id = ?)";

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM genres WHERE id = ?";

    public GenreDBStorage(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<Genre> getAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public void saveFilmGenre(long filmId, long genreId) {
        try {
            insertWithNoResult(INSERT_QUERY, filmId, genreId);
        } catch (DuplicateKeyException e) {
            log.info("Найден дубликат (filmId: {} genreId: {})", filmId, genreId);
        }
    }

    @Override
    public Collection<Genre> getFilmGenres(long filmId) {
        return findMany(FIND_FILM_GENRES_QUERY, filmId);
    }

    @Override
    public Optional<Genre> getGenre(long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }
}
