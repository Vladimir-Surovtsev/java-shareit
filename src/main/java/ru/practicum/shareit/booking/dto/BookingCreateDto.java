package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingCreateDto {
    @NotNull(message = "Booking start can't be blank")
    @Future(message = "Start date must be in the future")
    private LocalDateTime start;
    @NotNull(message = "Booking end can't be blank")
    @Future(message = "End date must be in the future")
    private LocalDateTime end;
    @NotNull(message = "Item id can't be blank")
    private Long itemId;
}
