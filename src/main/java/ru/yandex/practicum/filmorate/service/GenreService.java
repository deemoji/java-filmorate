package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.Collection;

@Service
public class GenreService {
    private final GenreStorage storage;

    @Autowired
    public GenreService(GenreStorage storage) {
        this.storage = storage;
    }

    public Collection<GenreDto> getAll() {
        return storage.getAll().stream()
                .map(GenreMapper::mapToDto)
                .toList();
    }

    public GenreDto getGenre(long id) {
        return storage.getGenre(id)
                .map(GenreMapper::mapToDto)
                .orElseThrow(() -> new NotFoundException("Жанр с id " + id + "не найден"));
    }
}
