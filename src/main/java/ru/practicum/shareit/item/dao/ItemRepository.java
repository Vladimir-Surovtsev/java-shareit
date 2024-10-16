package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemRepository {
    Collection<Item> getAllByUsersId(long userId);

    Item add(long userId, Item item);

    Item update(Item item);

    void delete(long userId, long itemId);

    Optional<Item> getById(long itemId);

    Collection<Item> getAllByText(String text);
}
