package ru.yandex.practicum.filmorate.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controllers.FilmController;
import ru.yandex.practicum.filmorate.exceptions.UserValidateException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
public class FilmControllerTest {
    Film film;
    FilmController filmController;

    @BeforeEach
    public void beforeEach() {
        filmController = new FilmController();
    }

    @Test
    public void createFilm() {
        film = Film.builder()
                .name("Барби")
                .description("Барби выгоняют из Барбиленда, потому что она не соответствует его нормам красоты.А ЕЩЕ ТАМ ЕСТЬ" +
                        " РАЙН ГОСЛИНГ")
                .releaseDate(LocalDate.of(2023,7,22))
                .duration(6840)
                .build();
        Film filmSave = filmController.createFilm(film);
        assertEquals(film,filmSave,"Фильмы не совпадают");
        assertEquals(1,filmController.getAllFilms(),"Количество фильмов в хранилище неверно");
    }

    @Test
    public void shouldNotCreateNameIsEmpty() {
        film = Film.builder() // фильм без имени
                .name("")
                .description("Барби выгоняют из Барбиленда, потому что она не соответствует его нормам красоты.А ЕЩЕ ТАМ ЕСТЬ" +
                        " РАЙН ГОСЛИНГ")
                .releaseDate(LocalDate.of(2023,7,22))
                .duration(6840)
                .build();
        final UserValidateException e = assertThrows(UserValidateException.class, () -> filmController.createFilm(film));
        assertEquals("Наименование фильма не может быть пустым", e.getMessage());
        assertEquals(0, filmController.getAllFilms().size(), "Список должен быть пустым");
    }

    @Test
    public void shouldNotCreateFilmDescriptionMoreThan200() {
        film = Film.builder() // фильм с длинным описанием
                .name("Барби")
                .description("Барби выгоняют из Барбиленда, потому что она не соответствует его нормам красоты. Тогда она начинает новую жизнь в реальном мире, где обнаруживает, что совершенства можно достичь только благодаря внутренней гармонии.")
                .releaseDate(LocalDate.of(2023,7,22))
                .duration(6840)
                .build();
        final UserValidateException e = assertThrows(UserValidateException.class, () -> filmController.createFilm(film));
        assertEquals("Описание не должно быть длинее 200 символов", e.getMessage());
        assertEquals(0, filmController.getAllFilms().size(), "Список должен быть пустым");
    }

    @Test
    public void shouldNotCreateFilmDateIsBefore() {
        film = Film.builder() // фильм с длинным описанием
                .name("")
                .description("Барби выгоняют из Барбиленда, потому что она не соответствует его нормам красоты.А ЕЩЕ ТАМ ЕСТЬ" +
                        " РАЙН ГОСЛИНГ")
                .releaseDate(LocalDate.of(1895, 12, 27))
                .duration(6840)
                .build();
        final UserValidateException e = assertThrows(UserValidateException.class, () -> filmController.createFilm(film));
        assertEquals("Дата релиза не должна быть раньше 28 декабря 1895 года", e.getMessage());
        assertEquals(0, filmController.getAllFilms().size(), "Список должен быть пустым");
    }

    @Test
    public void shouldNotCreateFilmDurationIsWrong() {
        film = Film.builder()
                .name("Барби")
                .description("Барби выгоняют из Барбиленда, потому что она не соответствует его нормам красоты.А ЕЩЕ ТАМ ЕСТЬ" +
                        " РАЙН ГОСЛИНГ")
                .releaseDate(LocalDate.of(2023,7,22))
                .duration(-1)
                .build();
        final UserValidateException e = assertThrows(UserValidateException.class, () -> filmController.createFilm(film));
        assertEquals("Продолжительность фильма не может быть отрицательным числом", e.getMessage());
        assertEquals(0, filmController.getAllFilms().size(), "Список должен быть пустым");
    }
}
