package ru.yandex.practicum.filmorate.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controllers.UserController;
import ru.yandex.practicum.filmorate.exceptions.UserValidateException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserControllerTest {
    User user;
    UserController userController;

    @BeforeEach
    public void beforeEach() {
        userController = new UserController();
    }

    @Test
    public void createUser() {
        user = User.builder()
                .email("rayangosling@hollywood.com")
                .login("ken")
                .name("rayangosling")
                .birthday(LocalDate.of(1980,11,12))
                .build();
        User userSave = userController.createUser(user);
        assertEquals(user,userSave,"Вы не Райн Гослинг");
        assertEquals(1,userController.getAllUsers());
    }

    @Test
    public void shouldNotCreateEmailInvalid() {
        user = User.builder()
                .email("rayangoslinghollywood.com")
                .login("ken")
                .name("rayangosling")
                .birthday(LocalDate.of(1980,11,12))
                .build();
        final UserValidateException e = assertThrows(UserValidateException.class, () -> userController.createUser(user));
        assertEquals("Email должен содержать символ @", e.getMessage());
        assertEquals(0, userController.getAllUsers().size(), "Список должен быть пустым");
    }

    @Test
    public void shouldNotCreateLoginContainsSpaces() {
        user = User.builder()
                .email("rayangosling@hollywood.com")
                .login(" ken")
                .name("rayangosling")
                .birthday(LocalDate.of(1980,11,12))
                .build();
        final UserValidateException e = assertThrows(UserValidateException.class, () -> userController.createUser(user));
        assertEquals("Логин не может содержать пробелы", e.getMessage());
        assertEquals(0, userController.getAllUsers().size(), "Список должен быть пустым");
    }

    @Test
    public void shouldCreateNameEqualsLogin() {
        user = User.builder()
                .email("rayangosling@hollywood.com")
                .login("ken")
                .birthday(LocalDate.of(1996, 12, 9))
                .build();
        User userSaved = userController.createUser(user);
        assertEquals(user.getLogin(), user.getName(), "Имя не равно логину");
        assertEquals(1, userController.getAllUsers().size(), "Количество пользователей в хранилище неверно");
    }

    @Test
    public void shouldNotBeBurnBeforeNow() {
        user = User.builder()
                .email("rayangosling@hollywood.com")
                .login("ken")
                .name("rayangosling")
                .birthday(LocalDate.of(9999, 9, 9))
                .build();
        final UserValidateException e = assertThrows(UserValidateException.class, () -> userController.createUser(user));
        assertEquals("Дата рождения не может быть в будущем", e.getMessage());
        assertEquals(0, userController.getAllUsers().size(), "Список должен быть пустым");
    }
}