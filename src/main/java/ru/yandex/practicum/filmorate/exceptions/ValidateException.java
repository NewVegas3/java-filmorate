package ru.yandex.practicum.filmorate.exceptions;

public class ValidateException extends IllegalArgumentException {
   public ValidateException(String message) {
       super(message);
   }
}
