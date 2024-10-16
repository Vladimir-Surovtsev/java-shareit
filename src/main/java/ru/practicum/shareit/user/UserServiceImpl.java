package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private static final int MAX_SIZE = 30;

    @Override
    public Collection<UserDto> getAll() {
        Collection<User> users = userRepository.getAll();
        return users.stream()
                .map(UserMapper.INSTANCE::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getById(long userId) {
        User user = userRepository.getById(userId)
                .orElseThrow(() -> {
                    log.info("GET Пользователь с id={} не найден", userId);
                    return new NotFoundException("Пользователя с id=" + userId + " не существует");
                });
        return UserMapper.INSTANCE.toUserDto(user);
    }

    @Override
    public UserDto create(UserCreateDto userCreateDto) {
        User user = userRepository.create(UserMapper.INSTANCE.toUser(userCreateDto));
        return UserMapper.INSTANCE.toUserDto(user);
    }

    @Override
    public UserDto update(long userId, UserUpdateDto userUpdateDto) {
        User userToUpdate = userRepository.getById(userId)
                .orElseThrow(() -> {
                    log.info("UPDATE-USER Пользователь с id={} не найден", userId);
                    return new NotFoundException("Пользователя с id=" + userId + " не существует");
                });
        User user = UserMapper.INSTANCE.toUser(userUpdateDto);
        if (user.getName() != null && !user.getName().isBlank()
                && user.getName().length() <= MAX_SIZE) {
            userToUpdate.setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().isBlank()
                && user.getEmail().length() <= MAX_SIZE
                && !userToUpdate.getEmail().equals(user.getEmail())) {
            userRepository.existsByEmail(user.getEmail(), userToUpdate.getEmail());
            userToUpdate.setEmail(user.getEmail());
        }
        return UserMapper.INSTANCE.toUserDto(userRepository.update(userId, userToUpdate));
    }

    @Override
    public void delete(long userId) {
        checkUserExistence(userId);
        userRepository.delete(userId);
    }

    private void checkUserExistence(Long userId) {
        userRepository.getById(userId)
                .orElseThrow(() -> {
                    log.info("Пользователь с id={} не найден", userId);
                    return new NotFoundException("Пользователя с id=" + userId + " не существует");
                });
    }
}