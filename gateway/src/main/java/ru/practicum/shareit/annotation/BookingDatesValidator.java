package ru.practicum.shareit.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.practicum.shareit.booking.dto.BookingCreateDto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class BookingDatesValidator implements ConstraintValidator<ValidBookingDates, BookingCreateDto> {
    @Override
    public void initialize(ValidBookingDates constraintAnnotation) {
    }

    @Override
    public boolean isValid(BookingCreateDto bookingCreateDto, ConstraintValidatorContext context) {
        if (bookingCreateDto == null) {
            return true;
        }
        LocalDateTime now = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
        if (bookingCreateDto.getStart() == null || bookingCreateDto.getEnd() == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Date cannot be empty")
                    .addConstraintViolation();
            return false;
        }
        if (bookingCreateDto.getStart().isBefore(now)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Start date and time should not be in the past")
                    .addConstraintViolation();
            return false;
        }
        if (bookingCreateDto.getEnd().isBefore(now)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("End date and time should not be in the past")
                    .addConstraintViolation();
            return false;
        }
        if (!bookingCreateDto.getStart().isBefore(bookingCreateDto.getEnd())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Start date of the booking must be before the end date of the booking")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
