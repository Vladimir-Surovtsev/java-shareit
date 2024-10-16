package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.Collection;

public interface ItemService {
    Collection<ItemDto> getAllByUsersId(long userId);

    ItemDto create(long userId, ItemCreateDto itemCreateDto);

    ItemDto update(long userId, long itemId, ItemUpdateDto itemUpdateDto);

    void delete(long userId, long itemId);

    ItemDto getById(long itemId);

    Collection<ItemDto> getAllByText(String text);
}
