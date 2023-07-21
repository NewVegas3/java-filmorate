package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.IdFilm;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validators.UserValidator;

import java.util.List;

@Slf4j
@RestController
public class UserController extends Controller {

    UserValidator userValidator = new UserValidator();

    @PutMapping(value = "/user")
    public User createUser(@RequestBody User user) {
        userValidator.validator(user);
        log.info("Получен POST-запрос к эндпоинту: '/user', пользователь добавлен");
        return (User) createUser(user);
    }

    @PutMapping(value = "/user")
    public User updateUser(@RequestBody User user) {
        userValidator.validator(user);
        log.info("Получен PUT-запрос к эндпоинту: '/user', пользователь обновлен");
        return (User) updateUser(user);
    }

    @GetMapping("/users")
    public List<IdFilm> getAllUsers() {
        return getAll();
    }
}
