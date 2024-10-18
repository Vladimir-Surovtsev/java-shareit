package ru.practicum.shareit.item.controller;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;
import java.util.Collections;

import static ru.practicum.shareit.constants.UserIdHttpHeader.USER_ID_HEADER;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public Collection<ItemInfoDto> getAllByUsersId(@RequestHeader(USER_ID_HEADER) long userId) {
        return itemService.getAllByUsersId(userId);
    }

    @GetMapping("/{itemId}")
    public ItemInfoDto getById(@RequestHeader(USER_ID_HEADER) long userId,
                               @PathVariable long itemId) {
        return itemService.getById(userId, itemId);
    }

    @PostMapping
    public ItemDto create(@RequestHeader(USER_ID_HEADER) long userId,
                          @Validated @RequestBody ItemCreateDto itemCreateDto) {
        return itemService.create(userId, itemCreateDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(USER_ID_HEADER) long userId,
                          @PathVariable long itemId,
                          @Validated @RequestBody ItemUpdateDto itemUpdateDto) {
        return itemService.update(userId, itemId, itemUpdateDto);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@RequestHeader(USER_ID_HEADER) long userId,
                       @PathVariable long itemId) {
        itemService.delete(itemId, userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> getAllByText(@RequestParam String text) {
        if (text.isEmpty()) {
            return Collections.emptyList();
        }
        return itemService.getAllByText(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(USER_ID_HEADER) long userId,
                                 @PathVariable long itemId, @RequestBody CommentDto commentDto) {
        String text = commentDto.getText();
        if (text == null || text.isEmpty()) {
            throw new ValidationException("Комментарий не может быть пустым");
        }
        return itemService.addComment(itemId, userId, commentDto);
    }
}
