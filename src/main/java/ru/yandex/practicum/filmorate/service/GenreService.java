package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.genre.GenreResponseDto;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.mapper.dto.GenreDtoMapper;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GenreService {
    private final GenreStorage genreStorage;
    private final GenreDtoMapper genreDtoMapper;

    public GenreService(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
        this.genreDtoMapper = new GenreDtoMapper();
    }

    public GenreResponseDto getGenreById(int id) {
        log.debug("getGenreById {}", id);
        return genreDtoMapper.toGenreResponse(genreStorage.findById(id).orElseThrow(() -> {
            log.error("getGenreById {} not found", id);
            return new GenreNotFoundException("Genre with id - " + id + " not found");
        }));
    }

    public List<GenreResponseDto> getGenres() {
        log.debug("getGenres");
        return genreStorage.findAll().stream().map(genreDtoMapper::toGenreResponse).collect(Collectors.toList());
    }
}
