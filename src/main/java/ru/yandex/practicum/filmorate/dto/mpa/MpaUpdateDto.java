package ru.yandex.practicum.filmorate.dto.mpa;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MpaUpdateDto {
    @NotNull
    private Long id;
}
