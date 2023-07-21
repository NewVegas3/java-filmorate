package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Data
@Builder
public class User extends IdFilm {
    @NonNull
    @Email
    private final String email;
    @NonNull
    @Pattern(regexp = "\\S*$")
    private final String login;
    private String name;
    @Past
    private final LocalDate birthday;
}