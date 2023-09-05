package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.FilmStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final String SQL_FOR_FILM_WITH_MPA =
            "SELECT * FROM film " +
                    "JOIN (SELECT film_rating.film_id, film_rating.rating_id, rating.name AS rating_name " +
                    "      FROM film_rating " +
                    "      JOIN rating ON film_rating.rating_id = rating.rating_id) AS add " +
                    "ON film.film_id = add.film_id";

    @Override
    public Optional<Film> findFilmById(int id) {
        // Запрос к базе данных для поиска фильма по идентификатору
        String sqlQuery = SQL_FOR_FILM_WITH_MPA + " WHERE film.film_id = ?";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sqlQuery, id);

        if (filmRows.next()) {
            // Извлечение данных из результатов запроса
            int filmId = filmRows.getInt("film_id");
            String name = filmRows.getString("name");
            String description = filmRows.getString("description");
            LocalDate releaseDate = filmRows.getDate("release_date").toLocalDate();
            int duration = filmRows.getInt("duration");
            int ratingId = filmRows.getInt("rating_id");
            String ratingName = filmRows.getString("rating_name");
            // Создание объекта Film с полученными данными
            Film film = new Film(filmId, name, description, releaseDate, duration, getRate(id), new Mpa(ratingId, ratingName), getGenres(id));
            // Вывод информации в лог о найденном фильме
            log.info("Найден фильм: {} {}", film.getId(), film.getName());
            return Optional.of(film);
        } else {
            // Вывод информации в лог, если фильм не найден
            log.info("Фильм с идентификатором {} не найден.", id);
            throw new ObjectNotFoundException("Фильм с идентификатором " + id + " не найден.");
        }
    }

    @Override
    public Collection<Film> findAllFilms() {
        return jdbcTemplate.query(SQL_FOR_FILM_WITH_MPA, (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public Film createFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("film").usingGeneratedKeyColumns("film_id");
        film.setId(simpleJdbcInsert.executeAndReturnKey(film.toMap()).intValue());

        if (film.getMpa() != null) {
            String sqlQuery = "insert into film_rating(film_id, rating_id) values (?, ?)";
            jdbcTemplate.update(sqlQuery, film.getId(), film.getMpa().getId());
        }

        if (film.getGenres() != null) {
            batchUpdateGenres(film);
        }

        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "update film set name = ?, description = ?, release_date = ?, duration = ? where film_id = ?";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getId());

        if (film.getRate() != 0) {
            String sqlRate = "update film set rate = ? where film_id = ?";
            jdbcTemplate.update(sqlRate, film.getRate(), film.getId());
        } else {
            String sqlRate = "update film set rate = ? where film_id = ?";
            jdbcTemplate.update(sqlRate, getRate(film.getId()), film.getId());
        }

        if (film.getMpa() != null) {
            String sqlMpa = "update film_rating set rating_id = ? where film_id = ?";
            jdbcTemplate.update(sqlMpa, film.getMpa().getId(), film.getId());
        }

        if (film.getGenres() != null) {
            updateGenres(film);
        }

        log.info("Фильм с id: {} обновлен", film.getId());
        return findFilmById(film.getId()).get();
    }

    @Override
    public void addLike(int filmId, int userId) {
        SqlRowSet checkRows = jdbcTemplate.queryForRowSet("select * from film_user where film_id = ? and user_id = ?", filmId, userId);
        if (checkRows.next()) {
            throw new ValidateException("Вы уже поставили лайк этому фильму");
        } else {
            String sqlQuery = "insert into film_user(film_id, user_id) values (?, ?)";
            jdbcTemplate.update(sqlQuery, filmId, userId);
            updateFilmRate(filmId);
            log.info("Пользователь с id: {} поставил лайк фильму с id: {}", userId, filmId);
        }
    }

    @Override
    public void removeLike(int filmId, int userId) {
        SqlRowSet checkRows = jdbcTemplate.queryForRowSet("select * from film_user where film_id = ? and user_id = ?", filmId, userId);
        log.info("Проверка поставленных лайков пользователя с id: {} фильму с id {}", userId, filmId);
        if (checkRows.next()) {
            String sql = "delete from film_user where film_id = ? and user_id = ?";
            jdbcTemplate.update(sql, filmId, userId);
            updateFilmRate(filmId);
            log.info("Пользователь с id: {} удалил лайк фильму с id: {}", userId, filmId);
        } else {
            throw new ValidateException("Вы не ставили лайк этому фильму");
        }
    }

    @Override
    public Collection<Film> getPopular(int count) {
        String sql = SQL_FOR_FILM_WITH_MPA + " order by rate limit ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), count);
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        int id = rs.getInt("film_id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        int duration = rs.getInt("duration");
        int rate = getRate(id);
        int ratingId = rs.getInt("rating_id");
        String ratingName = rs.getString("rating_name");
        return new Film(id, name, description, releaseDate, duration, rate, new Mpa(ratingId, ratingName), getGenres(id));
    }

    private Collection<Genre> getGenres(int filmId) {
        String sql = "select * from film_genre join genre on film_genre.genre_id = genre.genre_id where film_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Genre(rs.getInt("genre_id"), rs.getString("name")), filmId);
    }

    private int getRate(int filmId) {
        int rate = 0;
        SqlRowSet rateRows = jdbcTemplate.queryForRowSet("select count(user_id) as count from film_user where film_id = ?", filmId);
        if (rateRows.next()) {
            rate = rateRows.getInt("count");
        }
        return rate;
    }

    private void batchUpdateGenres(Film film) {
        int[] updateCounts = jdbcTemplate.batchUpdate(
                "insert into film_genre(film_id, genre_id) values (?, ?)",
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setInt(1, film.getId());
                        ps.setInt(2, getUniqueGenres(film.getGenres()).get(i));
                    }

                    public int getBatchSize() {
                        return getUniqueGenres(film.getGenres()).size();
                    }
                });
    }

    private void updateGenres(Film film) {
        String deleteSql = "delete from film_genre where film_id = ?";
        jdbcTemplate.update(deleteSql, film.getId());
        batchUpdateGenres(film);
    }

    private List<Integer> getUniqueGenres(Collection<Genre> genres) {
        Set<Integer> uniqueGenresIds = new HashSet<>();
        for (Genre genre : genres) {
            uniqueGenresIds.add(genre.getId());
        }
        return new ArrayList<>(uniqueGenresIds);
    }

    private void updateFilmRate(int filmId) {
        String sqlRating = "update film set rate = ? where film_id = ?";
        jdbcTemplate.update(sqlRating, getRate(filmId), filmId);
    }
}