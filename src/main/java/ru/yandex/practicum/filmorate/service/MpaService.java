package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.mpa.MpaResponseDto;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.mapper.dto.MpaDtoMapper;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MpaService {
    private final MpaStorage mpaStorage;
    private final MpaDtoMapper mpaDtoMapper;

    public MpaService(MpaStorage mpaStorage, MpaDtoMapper mpaDtoMapper) {
        this.mpaStorage = mpaStorage;
        this.mpaDtoMapper = mpaDtoMapper;
    }

    public MpaResponseDto getMpaById(int id) {
        log.debug("getMpaById {}", id);
        return mpaDtoMapper.toMpaResponse(mpaStorage.findById(id).orElseThrow(() -> {
            log.error("getMpaById {} not found", id);
            return new MpaNotFoundException("Mpa with id - " + id + " not found");
        }));
    }

    public List<MpaResponseDto> getAllMpa() {
        log.debug("getAllMpa");
        return mpaStorage.findAll().stream().map(mpaDtoMapper::toMpaResponse).collect(Collectors.toList());
    }
}
