package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exceptions.FilmObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.IdFilm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class Controller  {
    private Map<Integer, IdFilm> IdFilms = new HashMap<>();
    private int nextId = 1;

    public IdFilm create(IdFilm idFilm) {
        idFilm.setId(nextId);
        nextId++;
        return IdFilms.put(idFilm.getId(),idFilm);
    }

    public IdFilm update(IdFilm idFilm) {
        if (IdFilms.containsKey(idFilm.getId())) {
            IdFilms.put(idFilm.getId(),idFilm);
        } else {
            log.info("Фильм не обновлен, так как в хранилище нет фильмов с заданным id");
            throw new FilmObjectNotFoundException("Фильм с id: " + idFilm.getId() + "не найден");
        }
        return idFilm;
    }

    List<IdFilm> getAll() {
        return new ArrayList<>(IdFilms.values());
    }
}
