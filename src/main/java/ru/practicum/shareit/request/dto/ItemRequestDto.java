package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotNull;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class ItemRequestDto {
    @NotNull
    private Long id;
    private String description;
    @NotNull
    private User requestor;
    @NotNull
    private LocalDateTime created;
}
