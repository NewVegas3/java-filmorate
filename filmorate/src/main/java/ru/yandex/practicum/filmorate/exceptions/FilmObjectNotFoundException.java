package ru.yandex.practicum.filmorate.exceptions;

public class FilmObjectNotFoundException extends NullPointerException {
    public FilmObjectNotFoundException(String message) {
        super(message);
    }
}
