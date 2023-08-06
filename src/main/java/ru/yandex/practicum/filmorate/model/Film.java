package ru.yandex.practicum.filmorate.model;


import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import ru.yandex.practicum.filmorate.validators.AfterDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class Film extends Entity {
    @NonNull
    @NotBlank
    private final String name;
    @Size(min = 1, max = 200)
    private final String description;
    @AfterDate
    private final LocalDate releaseDate;
    @Positive
    private final int duration;
    private final Set<Integer> likes = new HashSet<>();
}
