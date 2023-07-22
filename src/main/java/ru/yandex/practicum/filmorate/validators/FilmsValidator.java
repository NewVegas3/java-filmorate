package ru.yandex.practicum.filmorate.validators;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;

@Slf4j
public class FilmsValidator {
    public void validator(Film film) throws ObjectNotFoundException {
        if (film.getName().equals("")) {
            log.info("Название фильма не может быть пустым");
            throw new ValidateException("Название фильма не может быть пустым");
        } else if (film.getDescription().length() > 200) {
            log.info("Максимальная длина описания — 200 символов");
            throw new ValidateException("Максимальная длина описания — 200 символов");
        } else if (film.getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER,28))) {
            log.info("Дата релиза  — не раньше 28 декабря 1895 года");
            throw new ValidateException("Дата релиза  — не раньше 28 декабря 1895 года");
        } else if (film.getDuration() < 0) {
            log.info("Продолжительность фильма не может быть отрицательным числом");
            throw new ValidateException("Продолжительность фильма не может быть отрицательным числом");
        }
    }
}
