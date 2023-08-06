package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Entity;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
public class UserController {
    private final InMemoryUserStorage userStorage;
    private final UserService userService;

    public UserController(UserService userService, InMemoryUserStorage userStorage) {
        this.userService = userService;
        this.userStorage = userStorage;
    }

    @PostMapping(value = "/users")
    public User createUser(@Valid @RequestBody User user) { // объект User передается в теле запроса (без id), если поля объекта не заполнены произойдёт ошибка
        log.info("Получен POST-запрос к эндпоинту: '/user'");
        return userStorage.create(user);
    }

    @PutMapping(value = "/users")
    public User updateUser(@Valid @RequestBody User user) { // объект User передается в теле запроса
        log.info("Получен PUT-запрос к эндпоинту: '/user'");
        return userStorage.update(user);
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userStorage.getAllValues();
    }

    @GetMapping("/users/{id}")
    public Entity getUser(@PathVariable int id) {
        return userStorage.getEntity(id);
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public void addFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("Получен PUT-запрос к эндпоинту: '/user'/{id}/friends/{friendId}'");
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("Получен DELETE-запрос к эндпоинту: '/user'/{id}/friends/{friendId}'");
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/users/{id}/friends")
    public List<User> getListOfUsersFriends(@PathVariable int id) {
        log.info("Получен GET-запрос к эндпоинту: '/users/{id}/friends'");
        User user = userStorage.getEntity(id);
        return userStorage.getListByIds(user.getFriends());
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> getListOfCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        log.info("Получен GET-запрос к эндпоинту: '/users/{id}/friends/common/{otherId}'");
        return userService.getListOfFriends(id, otherId);
    }
}
