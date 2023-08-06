package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Entity;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
public class FilmController {
    private final InMemoryFilmStorage filmStorage;
    private final FilmService filmService;

    public FilmController(InMemoryFilmStorage filmStorage, FilmService filmService) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }

    @PostMapping(value = "/films")
    public Film createFilm(@Valid @RequestBody Film film) { // объект Film передается в теле запроса (без id), если поля объекта не заполнены произойдёт ошибка
        log.info("Получен POST-запрос к эндпоинту: '/film'");
        return filmStorage.create(film);
    }

    @PutMapping(value = "/films")
    public Film updateFilm(@Valid @RequestBody Film film) { // объект Film передается в теле запроса, если поля объекта не заполнены произойдёт ошибка
        log.info("Получен PUT-запрос к эндпоинту: '/film'");
        return filmStorage.update(film);
    }

    @GetMapping("/films")
    public List<Film> getAllFilms() {
        return filmStorage.getAllValues();
    }


    @GetMapping("/films/{id}")
    public Entity getUser(@PathVariable int id) {
        return filmStorage.getEntity(id);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Получен PUT-запрос к эндпоинту: '/films/{id}/like/{userId}'");
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void removeLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Получен DELETE-запрос к эндпоинту: '/films/{id}/like/{userId}'");
        filmService.removeLike(id, userId);
    }

    @GetMapping("/films/popular")
    public List<Film> getListOfPopularFilms(@RequestParam(defaultValue = "10") String count) {
        log.info("Получен GET-запрос к эндпоинту: '/films/popular?count={count}'");
        return filmService.getListOfTopRatedFilms(count);
    }
}
