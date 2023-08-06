package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final InMemoryFilmStorage filmStorage;
    private final InMemoryUserStorage userStorage;

    public FilmService(InMemoryFilmStorage filmStorage, InMemoryUserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(int filmId, int userId) {
        checkId(filmId, filmStorage);
        checkId(userId, userStorage);
        Film film = filmStorage.getEntity(filmId);
        film.getLikes().add(userId);
    }

    public void removeLike(int filmId, int userId) {
        checkId(filmId, filmStorage);
        checkId(userId, userStorage);
        Film film = filmStorage.getEntity(filmId);
        film.getLikes().remove(Integer.valueOf(userId));
    }

    public List<Film> getListOfTopRatedFilms(String count) {
        int countAsInt = Integer.parseInt(count);
        return filmStorage.getAllValues()
                .stream()
                .sorted((film1,film2) -> film2.getLikes().size() - film1.getLikes().size())
                .limit(countAsInt)
                .collect(Collectors.toList());
    }

    private void checkId(int id, InMemoryStorage storage) {
        if (!storage.getAllKeys().contains(id)) {
            throw new ObjectNotFoundException("Пользователь с id " + id + " не найден");
        }
    }
}
