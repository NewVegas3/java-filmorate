package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Entity;

import java.util.*;

@Component
@Slf4j
public class InMemoryStorage<T extends Entity> {
    private final Map<Integer, T> entities = new HashMap<>();
    private int nextId = 1;

    public T create(T entity) {
        entity.setId(nextId);
        nextId++;
        entities.put(entity.getId(), entity);
        return entity;
    }

    public T update(T entity) {
        if (entities.containsKey(entity.getId())) {
            entities.put(entity.getId(), entity);
        } else {
            log.info("Фильм не обновлен, так как в хранилище нет фильмов с заданным id");
            throw new ObjectNotFoundException("Фильм с id: " + entity.getId() + "не найден");
        }
        return entity;
    }

    public T getEntity(int id) {
        if (!entities.containsKey(id)) {
            throw new ObjectNotFoundException("Объект не найден");
        }
        return entities.get(id);
    }

    public List<T> getListByIds(Set<Integer> ids) {
        List<T> resultList =  new ArrayList<>();
        for (Integer id : ids) {
            resultList.add(entities.get(id));
        }
        return resultList;
    }

    public List<Integer> getAllKeys() {
        return new ArrayList<>(entities.keySet());
    }

    public List<T> getAllValues() {
        return new ArrayList<>(entities.values());
    }

}
