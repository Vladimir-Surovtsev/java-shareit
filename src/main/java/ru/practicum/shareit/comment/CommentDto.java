package ru.practicum.shareit.comment;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {
    private Long id;
    @Size(max = 150, message = "Комментарий не должен превышать 150 символов")
    private String text;
    private Long itemId;
    private String authorName;
    private LocalDateTime created;
}
