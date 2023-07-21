package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exceptions.FilmObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.IdFilm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class Controller {
    private Map<Integer, IdFilm> idFilms = new HashMap<>();
    private int nextId = 1;

    public IdFilm create(IdFilm idFilm) {
        idFilm.setId(nextId);
        nextId++;
        idFilms.put(idFilm.getId(),idFilm);
        return idFilm;
    }

    public IdFilm update(IdFilm idFilm) {
        if (idFilms.containsKey(idFilm.getId())) {
            idFilms.put(idFilm.getId(),idFilm);
        } else {
            log.info("Фильм не обновлен, так как в хранилище нет фильмов с заданным id");
            throw new FilmObjectNotFoundException("Фильм с id: " + idFilm.getId() + "не найден");
        }
        return idFilm;
    }

    List<IdFilm> getAll() {
        return new ArrayList<>(idFilms.values());
    }
}
