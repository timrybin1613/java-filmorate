package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.genre.GenreResponseDto;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/genres")
public class GenreController {
    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping("/{id}")
    public GenreResponseDto getGenre(@PathVariable Integer id) {
        log.debug("getGenre - {}", id);
        return genreService.getGenreById(id);
    }

    @GetMapping
    public List<GenreResponseDto> getGenres() {
        log.debug("getGenres");
        List<GenreResponseDto> genres = genreService.getGenres();
        log.debug("genres size : {}", genres.size());
        return genres;
    }
}
