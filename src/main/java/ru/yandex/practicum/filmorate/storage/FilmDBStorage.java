package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.Date;
import java.util.Collection;
import java.util.Optional;

@Repository
public class FilmDBStorage extends BaseRepository<Film> implements FilmStorage {

    private static final String FIND_ALL_QUERY = "SELECT * FROM films";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM films WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO films(" +
            "name, description, release_date, duration, age_rate_id" +
            ") VALUES(?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE films SET " +
            "name = ?, description = ?, release_date = ?, duration = ?, age_rate_id = ? WHERE id = ?";

    public FilmDBStorage(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<Film> getFilms() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Optional<Film> getFilm(long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    @Override
    public Film save(Film film) {
        long id = insert(INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpaId()
        );
        film.setId(id);
        return film;
    }

    @Override
    public Film update(Film film) {
        update(UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpaId(),
                film.getId()
        );
        return film;
    }
}
