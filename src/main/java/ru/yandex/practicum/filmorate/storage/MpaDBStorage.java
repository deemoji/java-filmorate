package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.Optional;

@Repository
public class MpaDBStorage extends BaseRepository<Mpa> implements MpaStorage {

    private static final String FIND_ALL_QUERY = "SELECT * FROM age_rates";

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM age_rates WHERE id = ?";

    public MpaDBStorage(JdbcTemplate jdbc, RowMapper<Mpa> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<Mpa> getAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Optional<Mpa> getMpa(long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }
}
