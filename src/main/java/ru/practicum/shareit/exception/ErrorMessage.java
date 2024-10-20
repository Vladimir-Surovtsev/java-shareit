package ru.practicum.shareit.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ErrorMessage {
    public String error;
    public String stackTrace;

    public ErrorMessage(String error) {
        this.error = error;
    }
}
