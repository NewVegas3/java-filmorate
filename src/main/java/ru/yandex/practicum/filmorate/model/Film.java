package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import ru.yandex.practicum.filmorate.validators.AfterDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.*;

@Data
@Builder
@AllArgsConstructor
public class Film {
    private int id;
    @NonNull
    @NotBlank
    private final String name;
    @Size(min = 1, max = 200)
    private final String description;
    @AfterDate
    private final LocalDate releaseDate;
    @Positive
    private final int duration;
    int rate;
    private Mpa mpa;
    private Collection<Genre> genres;

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", name);
        values.put("description", description);
        values.put("release_date", releaseDate);
        values.put("duration", duration);
        values.put("rate", rate);
        return values;
    }
}
