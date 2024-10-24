package ru.practicum.shareit.booking.dto;

import jakarta.validation.ValidationException;

import java.util.Arrays;

public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static BookingState from(String state) {
        return Arrays.stream(BookingState.values())
                .filter(bookingState -> bookingState.name().equals(state))
                .findAny().orElseThrow(() -> new ValidationException("Unknown state: " + state));
    }
}
