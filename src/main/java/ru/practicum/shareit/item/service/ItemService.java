package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.Collection;

public interface ItemService {
    Collection<ItemInfoDto> getAllByUsersId(long userId);

    ItemInfoDto getById(long userId, long itemId);

    ItemDto create(long userId, ItemCreateDto itemCreateDto);

    ItemDto update(long userId, long itemId, ItemUpdateDto itemUpdateDto);

    void delete(long itemId, long userId);

    Collection<ItemDto> getAllByText(String text);

    CommentDto addComment(long itemId, long userId, CommentDto commentDto);
}
