package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.mpa.MpaResponseDto;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mpa")
public class MpaController {
    private final MpaService mpaService;

    public MpaController(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    @GetMapping("/{id}")
    public MpaResponseDto getMpaById(@PathVariable int id) {
        log.debug("getMpaById - {}", id);
        return mpaService.getMpaById(id);
    }

    @GetMapping
    public List<MpaResponseDto> getAllMpa() {
        List<MpaResponseDto> mpas = mpaService.getAllMpa();
        log.debug("mpa`s size: {}", mpas.size());
        return mpaService.getAllMpa();
    }

}
