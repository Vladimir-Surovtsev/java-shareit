package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository {
    Collection<User> getAll();

    Optional<User> getById(long userId);

    User create(User user);

    User update(long userId, User user);

    void delete(long userId);

    void existsByEmail(String newEmail, String oldEmail);
}
