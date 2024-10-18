package ru.practicum.shareit.booking.service;

import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BookingDto create(Long userId, BookingCreateDto bookingCreateDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.info("ADD-BOOKING Пользователь с id={} не найден", userId);
                    return new NotFoundException("Пользователя с id=" + userId + " не существует");
                });
        Item item = itemRepository.findById(bookingCreateDto.getItemId())
                .orElseThrow(() -> {
                    log.info("ADD-BOOKING Предмет с id={} не найден", bookingCreateDto.getItemId());
                    return new NotFoundException("Предмета с id=" + bookingCreateDto.getItemId() + " не существует");
                });
        if (Boolean.FALSE.equals(item.getAvailable())) {
            throw new ValidationException("Предмет с id=" + bookingCreateDto.getItemId() + " недоступен для бронирования");
        }
        if (Objects.equals(item.getOwner().getId(), userId)) {
            throw new NotFoundException("У вас не найдено такой брони");
        }
        List<Booking> conflictingBookings = bookingRepository.findConflictingBookings(
                bookingCreateDto.getItemId(), bookingCreateDto.getStart(), bookingCreateDto.getEnd());
        if (!conflictingBookings.isEmpty()) {
            throw new ValidationException("Данное бронирование пересекается с существующими бронированиями");
        }
        Booking booking = BookingMapper.INSTANCE.toBooking(bookingCreateDto);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        return BookingMapper.INSTANCE.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDto updateStatus(Long ownerId, Long bookingId, boolean approved) {
        checkUserExistence(ownerId, "UPDATE-BOOKING-STATUS");
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    log.info("UPDATE-BOOKING-STATUS Аренды с id={} не найден", bookingId);
                    return new NotFoundException("Аренды с id=" + bookingId + " не существует");
                });
        if (!(booking.getItem().getOwner().getId().equals(ownerId))) {
            throw new NotFoundException("Вы не являетесь владельцем данного предмета!");
        }
        if (booking.getStatus() == BookingStatus.APPROVED) {
            throw new ValidationException("Нельзя подтвердить бронь, которая уже подтверждена.");
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return BookingMapper.INSTANCE.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getById(Long userId, Long bookingId) {
        checkUserExistence(userId, "GET-BOOKING-BY-ID");
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Не найдено брони с ID = " + bookingId));
        if (!(booking.getBooker().getId().equals(userId)) && !(booking.getItem().getOwner().getId().equals(userId))) {
            throw new NotFoundException("У вас не найдено такой брони или предмета.");
        }
        return BookingMapper.INSTANCE.toBookingDto(booking);
    }

    @Override
    public Collection<BookingDto> getAllByUserId(Long userId, BookingState state) {
        checkUserExistence(userId, "GET-ALL-BY-USER-ID");
        LocalDateTime now = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
        List<Booking> bookings = switch (state) {
            case CURRENT -> bookingRepository
                    .findAllByBookerIdAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(userId, now, now);
            case PAST -> bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, now);
            case FUTURE -> bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, now);
            case WAITING -> bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
            case REJECTED ->
                    bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
            default -> bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
        };
        return bookings.stream().map(BookingMapper.INSTANCE::toBookingDto).collect(Collectors.toList());
    }

    @Override
    public Collection<BookingDto> getAllByOwnerId(Long ownerId, BookingState state) {
        checkUserExistence(ownerId, "GET-ALL-BY-OWNER-ID");
        LocalDateTime now = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
        List<Booking> bookings = switch (state) {
            case CURRENT -> bookingRepository
                    .findAllByItemOwnerIdAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(ownerId, now, now);
            case PAST -> bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, now);
            case FUTURE -> bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, now);
            case WAITING ->
                    bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.WAITING);
            case REJECTED ->
                    bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.REJECTED);
            default -> bookingRepository.findAllByItemOwnerIdOrderByStartDesc(ownerId);
        };
        return bookings.stream().map(BookingMapper.INSTANCE::toBookingDto).collect(Collectors.toList());
    }

    private void checkUserExistence(Long userId, String method) {
        userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.info("{} Пользователь с id={} не найден", method, userId);
                    return new NotFoundException("Пользователя с id=" + userId + " не существует");
                });
    }
}
