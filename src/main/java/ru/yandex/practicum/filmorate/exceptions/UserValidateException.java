package ru.yandex.practicum.filmorate.exceptions;

public class UserValidateException extends IllegalArgumentException {
   public UserValidateException(String message) {
       super(message);
   }
}
