package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class InMemoryLikeStorage implements LikeStorage {

    private final Map<Long, Set<Long>> filmsAndLikes = new HashMap<>();

    @Override
    public void saveLike(Long filmId, Long userId) {
        Set<Long> filmLikes = filmsAndLikes.getOrDefault(filmId, new HashSet<>());
        filmLikes.add(userId);
        filmsAndLikes.put(filmId, filmLikes);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        Set<Long> filmLikes = filmsAndLikes.getOrDefault(filmId, new HashSet<>());
        filmLikes.remove(userId);
    }

    @Override
    public Collection<Long> getPopularFilms(int count) {
        return filmsAndLikes.keySet().stream()
                .sorted(Comparator.comparingInt(filmId -> {
                    return filmsAndLikes.getOrDefault(filmId, new HashSet<>()).size();
                }).reversed())
                .limit(count)
                .toList();
    }
}
