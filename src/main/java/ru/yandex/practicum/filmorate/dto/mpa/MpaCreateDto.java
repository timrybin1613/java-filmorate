package ru.yandex.practicum.filmorate.dto.mpa;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MpaCreateDto {
    @NotNull
    private Long id;
}
