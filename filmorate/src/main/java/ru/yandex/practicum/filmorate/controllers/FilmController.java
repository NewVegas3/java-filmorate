package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.IdFilm;
import ru.yandex.practicum.filmorate.validators.FilmsValidator;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
public class FilmController extends Controller {

    FilmsValidator filmsValidator = new FilmsValidator();

    @PostMapping(value = "/films")
    public Film createFilm(@Valid @RequestBody Film film) {
        filmsValidator.validator(film);
        log.info("Получен POST-запрос к эндпоинту: '/film', фильм добавлен");
        return (Film) create(film);
    }

    @PutMapping(value = "/films")
    public Film updateFilm(@Valid @RequestBody Film film) {
        filmsValidator.validator(film);
        log.info("Получен PUT-запрос к эндпоинту: '/film', фильм обновлен");
        return (Film) update(film);
    }

    @GetMapping(value = "/films")
    public List<IdFilm> getAllFilms() {
        return getAll();
    }
}

