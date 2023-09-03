package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {
    Collection<Film> findAllFilms();
    Optional<Film> findFilmById(int id);
    Film createFilm(Film film);
    Film updateFilm(Film film);
    void addLike(int filmId, int userId);
    void removeLike(int filmId, int userId);
    Collection<Film> getPopular(int count);
}
