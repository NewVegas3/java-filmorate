package ru.yandex.practicum.filmorate.model;


import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@Builder
public class Film extends Entity {
    @NonNull
    @NotBlank
    private String name;
    @Size(min = 1, max = 200)
    private final String description;
    private final LocalDate releaseDate;
    @Positive
    private final int duration;
}
