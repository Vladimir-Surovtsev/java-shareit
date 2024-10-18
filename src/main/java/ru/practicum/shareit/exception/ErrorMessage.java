package ru.practicum.shareit.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ErrorMessage {
    String error;

    String stackTrace;

    public ErrorMessage(String error) {
        this.error = error;
    }
}
