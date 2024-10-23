package ru.practicum.shareit.booking.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.Collection;

@Service
public interface BookingService {
    BookingDto create(long userId, BookingCreateDto bookingCreateDto);

    BookingDto updateStatus(long ownerId, long bookingId, boolean approved);

    BookingDto getById(long userId, long bookingId);

    Collection<BookingDto> getAllByUserId(long userId, BookingState state);

    Collection<BookingDto> getAllByOwnerId(long ownerId, BookingState state);
}
