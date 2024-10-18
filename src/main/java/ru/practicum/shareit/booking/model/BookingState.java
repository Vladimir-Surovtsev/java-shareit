package ru.practicum.shareit.booking.model;

import jakarta.validation.ValidationException;

public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static BookingState from(String state) {
        for (BookingState bookingState : BookingState.values()) {
            if (bookingState.name().equalsIgnoreCase(state)) {
                return bookingState;
            }
        }
        throw new ValidationException("Unknown state: " + state);
    }
}
