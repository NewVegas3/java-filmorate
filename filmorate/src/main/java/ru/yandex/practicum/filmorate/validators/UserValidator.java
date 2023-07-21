package ru.yandex.practicum.filmorate.validators;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exceptions.UserValidateException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@Slf4j
public class UserValidator {
    public void validator(User user) throws UserValidateException {
        if (user.getEmail().indexOf('@') < 0) {
            log.info("электронная почта не может быть пустой и должна содержать символ @");
            throw new UserValidateException("электронная почта не может быть пустой и должна содержать символ @");
        } else if (user.getLogin().contains(" ")) {
            log.info("логин не может быть пустым");
            throw new UserValidateException("логин не может быть пустым");
        } else if (user.getName() == null) {
            log.info("Имя пользователя не введенно, поэтому в поле Name будет сохранен ваш Логин");
            user.setName(user.getLogin());
        } else if (user.getBirthday() != null || user.getBirthday().isAfter(LocalDate.now())) {
            log.info("дата рождения не может быть в будущем.");
            throw new UserValidateException("дата рождения не может быть в будущем.");
        }
    }
}