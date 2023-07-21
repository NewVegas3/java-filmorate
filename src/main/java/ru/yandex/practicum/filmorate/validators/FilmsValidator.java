package ru.yandex.practicum.filmorate.validators;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exceptions.FilmObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

@Slf4j
public class FilmsValidator {
    public void validator(Film film) throws FilmObjectNotFoundException {
        if (film.getName().isBlank()) {
            log.info("Название фильма не может быть пустым");
            throw new FilmObjectNotFoundException("Название фильма не может быть пустым");
        } else if (film.getDescription().length() > 200) {
            log.info("Максимальная длина описания — 200 символов");
            throw new FilmObjectNotFoundException("Максимальная длина описания — 200 символов");
        } else if (film.getReleaseDate().isBefore(LocalDate.of(1985,12,28))) {
            log.info("Дата релиза  — не раньше 28 декабря 1895 года");
            throw new FilmObjectNotFoundException("Дата релиза  — не раньше 28 декабря 1895 года");
        } else if (film.getDuration() < 0) {
            log.info("Продолжительность фильма не может быть отрицательным числом");
            throw new FilmObjectNotFoundException("Продолжительность фильма не может быть отрицательным числом");
        }
    }
}
