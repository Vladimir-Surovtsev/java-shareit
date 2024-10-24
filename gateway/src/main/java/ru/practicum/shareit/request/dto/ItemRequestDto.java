package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ItemRequestDto {
    private Long id;
    @NotBlank(message = "Request description can't be blank")
    @Size(max = 150, message = "Request description should be no more than 150 characters long")
    private String description;
    private LocalDateTime created;
}
