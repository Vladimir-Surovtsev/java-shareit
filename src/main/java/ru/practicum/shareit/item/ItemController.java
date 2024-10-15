package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private static final String HEADER = "X-Sharer-User-Id";

    @GetMapping
    public Collection<ItemDto> findAll(@RequestHeader(HEADER) Long userId) {
        return itemService.findItemsByUserId(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemDto(@PathVariable Long itemId) {
        return itemService.findItemById(itemId);
    }

    @PostMapping
    public ItemDto create(@RequestHeader(HEADER) Long userId,
                          @Valid @RequestBody ItemDto itemDto) {
        return itemService.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(HEADER) Long userId,
                          @PathVariable Long itemId,
                          @RequestBody ItemDto itemDto) {
        return itemService.update(userId, itemId, itemDto);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItemDto(
            @RequestHeader(HEADER) Long userId,
            @RequestParam(defaultValue = "") String text) {
        return itemService.findItemsByText(text);
    }
}
