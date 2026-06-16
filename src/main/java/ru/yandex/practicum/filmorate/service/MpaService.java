package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.Collection;

@Service
public class MpaService {

    private final MpaStorage storage;

    @Autowired
    public MpaService(MpaStorage storage) {
        this.storage = storage;
    }

    public Collection<MpaDto> getAll() {
        return storage.getAll().stream()
                .map(MpaMapper::mapToDto)
                .toList();
    }

    public MpaDto getMpa(long id) {
        return storage.getMpa(id)
                .map(MpaMapper::mapToDto)
                .orElseThrow(() -> new NotFoundException("Рейтинг с id " + id + "не найден"));
    }
}
