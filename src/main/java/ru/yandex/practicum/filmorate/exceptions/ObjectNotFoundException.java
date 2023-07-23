package ru.yandex.practicum.filmorate.exceptions;

public class ObjectNotFoundException extends NullPointerException {
    public ObjectNotFoundException(String message) {
        super(message);
    }
}
