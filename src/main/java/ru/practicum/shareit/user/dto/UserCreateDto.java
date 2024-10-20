package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserCreateDto {
    @NotBlank(message = "User name can't be blank")
    @Size(max = 30, message = "User name should be no more than 30 characters long")
    private String name;
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email can't be blank")
    @Size(max = 254, message = "User email should be no more than 254 characters long")
    private String email;
}
