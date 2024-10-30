package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.Collections;

import static ru.practicum.shareit.constants.UserIdHttpHeader.USER_ID_HEADER;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getAllByUsersId(@RequestHeader(USER_ID_HEADER) long userId) {
        return itemClient.getAllByUsersId(userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(@RequestHeader(USER_ID_HEADER) long userId,
                                          @PathVariable long itemId) {
        return itemClient.getById(userId, itemId);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(USER_ID_HEADER) long userId,
                                         @Valid @RequestBody ItemCreateDto itemCreateDto) {
        return itemClient.create(userId, itemCreateDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader(USER_ID_HEADER) long userId,
                                         @PathVariable long itemId,
                                         @RequestBody ItemUpdateDto itemUpdateDto) {
        return itemClient.update(userId, itemId, itemUpdateDto);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@RequestHeader(USER_ID_HEADER) long userId,
                       @PathVariable long itemId) {
        itemClient.delete(itemId, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getAllByText(@RequestParam String text) {
        if (text.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        return itemClient.getAllByText(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(USER_ID_HEADER) long userId,
                                             @PathVariable long itemId,
                                             @Validated @RequestBody CommentDto commentDto) {
        return itemClient.addComment(itemId, userId, commentDto);
    }
}
