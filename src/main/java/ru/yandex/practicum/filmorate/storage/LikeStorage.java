package ru.yandex.practicum.filmorate.storage;

import java.util.Collection;

public interface LikeStorage {
    void saveLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);

    Collection<Long> getPopularFilms(int count);

}
