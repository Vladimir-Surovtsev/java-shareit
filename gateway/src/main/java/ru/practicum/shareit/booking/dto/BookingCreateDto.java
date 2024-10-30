package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.annotation.ValidBookingDates;

import java.time.LocalDateTime;

@Data
@Builder
@ValidBookingDates
public class BookingCreateDto {
    @NotNull(message = "Booking start can't be blank")
    @Future(message = "Start date must be in the future")
    private LocalDateTime start;
    @NotNull(message = "Booking end can't be blank")
    @FutureOrPresent(message = "Start date must be in the future or in the present")
    private LocalDateTime end;
    @NotNull(message = "Item id can't be blank")
    private Long itemId;
}
