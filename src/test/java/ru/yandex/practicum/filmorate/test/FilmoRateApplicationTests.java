package ru.yandex.practicum.filmorate.test;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.impl.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmoRateApplicationTests {
    private final UserDbStorage userStorage;

    @Test
    public void testCreateUser() {
        // Создаем пользователя для тестирования
        User user = User.builder()
                .email("mail@mail.ru")
                .login("Nick Name")
                .name("dolore")
                .birthday(LocalDate.of(1946, 8, 20))
                .build();

        // Проверяем, что пользователь успешно создан
        Optional<User> userCreated = Optional.of(userStorage.createUser(user));
        assertThat(userCreated)
                .isPresent()
                .hasValueSatisfying(createdUser -> assertThat(createdUser).hasFieldOrPropertyWithValue("id", 1));

        // Проверяем, что список всех пользователей содержит только одного пользователя
        List<User> allUsers = (List<User>) userStorage.findAllUsers();
        assertThat(allUsers.size()).isEqualTo(1);
        assertThat(allUsers).isEqualTo(List.of(user));
    }

    @Test
    public void testFindUserById() {
        // Ищем пользователя по ID и проверяем, что он найден
        Optional<User> userOptional = userStorage.findUserById(1);
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(foundUser -> assertThat(foundUser).hasFieldOrPropertyWithValue("id", 1));
    }
}
