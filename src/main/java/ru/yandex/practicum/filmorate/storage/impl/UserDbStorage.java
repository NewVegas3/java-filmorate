package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidateException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

@Slf4j
@Component
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<User> findAllUsers() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs));
    }

    @Override
    public Collection<User> findUsersFriends(int id) {
        String sql = "SELECT * FROM users WHERE user_id IN (SELECT friend_id FROM user_friend WHERE user_id = ?)";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), id);
    }

    @Override
    public Optional<User> findUserById(int id) {
        String query = "SELECT user_id, email, login, name, birthday FROM users WHERE user_id = ?";
        return jdbcTemplate.query(query, new Object[]{id}, resultSet -> {
            if (resultSet.next()) {
                User user = makeUser(resultSet);
                log.info("Найден пользователь: {} {}", user.getId(), user.getLogin());
                return Optional.of(user);
            } else {
                log.info("Пользователь с идентификатором {} не найден.", id);
                throw new ObjectNotFoundException("Пользователь с идентификатором " + id + " не найден.");
            }
        });
    }

    @Override
    public Collection<User> findCommonFriends(int id, int otherId) {
        String sql = "select * from users where user_id in (select friend_id from user_friend where user_id = ? and friend_id in (select friend_id from user_friend where user_id = ?))";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), id, otherId);
    }

    @Override
    public boolean removeUser(int id) {
        String sql = "delete from users WHERE user_id = ? ";
        log.info("Пользователь с id: {} удален", id);
        return jdbcTemplate.update(sql, id) > 0;
    }

    @Override
    public User createUser(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");
        Number userId = simpleJdbcInsert.executeAndReturnKey(user.toMap());
        user.setId(userId.intValue());
        log.info("Создан пользователь: {} {}", user.getId(), user.getLogin());
        return user;
    }

    @Override
    public User updateUser(User user) {
        String sql = "update users set email = ?, login = ?, name = ?, birthday = ? where user_id = ?";
        jdbcTemplate.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        log.info("Пользователь с id: {} обновлен", user.getId());
        return user;
    }

    @Override
    public void addFriend(int id, int friendId) {
        String sqlCheck = "SELECT COUNT(*) FROM user_friend WHERE (user_id = ? AND friend_id = ?) OR (user_id = ? AND friend_id = ?)";
        int friendCount = jdbcTemplate.queryForObject(sqlCheck, Integer.class, id, friendId, friendId, id);
        if (friendCount == 0) {
            String sql = "INSERT INTO user_friend (user_id, friend_id) VALUES (?, ?)";
            jdbcTemplate.update(sql, id, friendId);
            log.info("Пользователь с id: {} добавил в друзья пользователя с id: {}", id, friendId);
        } else if (friendCount == 2) {
            throw new ValidateException("Вы уже являетесь друзьями");
        } else {
            String sql = "SELECT user_id FROM user_friend WHERE user_id = ? AND friend_id = ?";
            Integer existingUserId = jdbcTemplate.queryForObject(sql, Integer.class, id, friendId);
            if (existingUserId != null) {
                throw new ValidateException("Вы уже отправили запрос этому пользователю");
            } else {
                jdbcTemplate.update("INSERT INTO user_friend (user_id, friend_id) VALUES (?, ?)", id, friendId);
                log.info("Пользователь с id: {} добавил в друзья пользователя с id: {}", id, friendId);
            }
        }
    }

    @Override
    public void removeFriend(int id, int friendId) {
        String sqlCheck = "SELECT COUNT(*) FROM user_friend WHERE (user_id = ? AND friend_id = ?) OR (user_id = ? AND friend_id = ?)";
        int friendCount = jdbcTemplate.queryForObject(sqlCheck, Integer.class, id, friendId, friendId, id);
        if (friendCount == 0) {
            throw new ValidateException("Вы не отправляли пользователю запрос на дружбу");
        } else {
            String sql = "DELETE FROM user_friend WHERE (user_id = ? AND friend_id = ?) OR (user_id = ? AND friend_id = ?)";
            jdbcTemplate.update(sql, id, friendId, friendId, id);
            log.info("Запрос на дружбу пользователя с id: {} к пользователю с id: {} удален", id, friendId);
        }
    }

    private User makeUser(ResultSet rs) throws SQLException {
        int id = rs.getInt("user_id");
        String email = rs.getString("email");
        String login = rs.getString("login");
        String name = rs.getString("name");
        LocalDate birthday = rs.getDate("birthday").toLocalDate();

        return new User(id, email, login, name, birthday);
    }
}
