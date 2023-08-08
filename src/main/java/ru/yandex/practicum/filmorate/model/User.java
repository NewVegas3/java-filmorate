package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Email;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class User extends Entity {
    @NonNull
    @Email
    private final String email;
    @NonNull
    @Pattern(regexp = "\\S*$")
    private final String login;
    private String name;
    @Past
    private final LocalDate birthday;
    private Set<Integer> friends;

    public User(String email, String login, String name, LocalDate birthday, Set<Integer> friends) {
        this.email = email;
        this.login = login;
        this.name = name;
        if ((name == null) || (name.isEmpty()) || (name.isBlank())) {
            this.name = login;
        }
        this.birthday = birthday;
        this.friends = friends;
        if (friends == null) {
            this.friends = new HashSet<>();
        }
    }
}
