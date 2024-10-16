package ru.practicum.shareit.user.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();
    private Long lastId = 0L;

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public Optional<User> getById(long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public User create(User user) {
        if (emails.contains(user.getEmail())) {
            throw new DuplicatedDataException("Email must be unique");
        }
        user.setId(++lastId);
        users.put(user.getId(), user);
        emails.add(user.getEmail());
        return user;
    }

    @Override
    public User update(long userId, User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void delete(long userId) {
        User user = users.remove(userId);
        if (user != null) {
            emails.remove(user.getEmail());
        }
    }

    @Override
    public void existsByEmail(String newEmail, String oldEmail) {
        if (emails.contains(newEmail)) {
            throw new DuplicatedDataException("Email must be unique");
        }
        emails.remove(oldEmail);
        emails.add(newEmail);
    }
}
