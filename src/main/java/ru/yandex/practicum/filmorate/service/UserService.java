package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryStorage;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {
    private final InMemoryStorage<User> userStorage;

    public UserService(InMemoryStorage<User> userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(int userId, int otherUserId) {
        if (userId == otherUserId) {
            throw new ValidationException("id пользователей не могут совпадать");
        }
        checkId(userId);
        checkId(otherUserId);
        User user1 = userStorage.getEntity(userId);
        User user2 = userStorage.getEntity(otherUserId);
        user1.getFriends().add(otherUserId);
        user2.getFriends().add(userId);
    }

    public void removeFriend(int userId, int otherUserId) {
        if (userId == otherUserId) {
            throw new ValidationException("id пользователей не могут совпадать");
        }
        checkId(userId);
        checkId(otherUserId);
        User user1 = userStorage.getEntity(userId);
        User user2 = userStorage.getEntity(otherUserId);
        user1.getFriends().remove(Integer.valueOf(otherUserId));
        user2.getFriends().remove(Integer.valueOf(userId));
    }

    public List<User> getListOfFriends(int userId, int otherUserId) {
        if (userId == otherUserId) {
            throw new ValidationException("id пользователей не могут совпадать");
        }
        checkId(userId);
        checkId(otherUserId);
        User user1 = userStorage.getEntity(userId);
        User user2 = userStorage.getEntity(otherUserId);
        Set<Integer> commonFriendsIds = new HashSet<>(user1.getFriends());
        commonFriendsIds.addAll(user2.getFriends());
        commonFriendsIds.remove(user1.getId());
        commonFriendsIds.remove(user2.getId());
        ArrayList<User> comFriends = new ArrayList<>();
        for (Integer id : commonFriendsIds) {
            if (userStorage.getAllKeys().contains(id)) {
                comFriends.add(userStorage.getEntity(id));
            }
        }
        return comFriends;
    }

    private void checkId(int id) {
        if (!userStorage.getAllKeys().contains(id)) {
            throw new ObjectNotFoundException("Пользователь с id " + id + " не найден");
        }
    }
}
