package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class Controller {
    private Map<Integer, Entity> entities = new HashMap<>();
    private int nextId = 1;

    public Entity create(Entity entity) {
        entity.setId(nextId);
        nextId++;
        entities.put(entity.getId(), entity);
        return entity;
    }

    public Entity update(Entity entity) {
        if (entities.containsKey(entity.getId())) {
            entities.put(entity.getId(), entity);
        } else {
            log.info("Фильм не обновлен, так как в хранилище нет фильмов с заданным id");
            throw new ObjectNotFoundException("Фильм с id: " + entity.getId() + "не найден");
        }
        return entity;
    }

    List<Entity> getAll() {
        return new ArrayList<>(entities.values());
    }
}
