package ru.yandex.practicum.filmorate.mapper.dto;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.mpa.MpaResponseDto;
import ru.yandex.practicum.filmorate.model.film.Mpa;

@Component
public class MpaDtoMapper {

    public MpaResponseDto toMpaResponse(Mpa mpa) {
        MpaResponseDto dto = new MpaResponseDto();
        dto.setId(mpa.getId());
        dto.setName(mpa.getName());
        return dto;
    }

}
