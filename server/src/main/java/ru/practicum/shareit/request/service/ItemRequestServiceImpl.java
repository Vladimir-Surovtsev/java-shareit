
package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDto create(long userId, ItemRequestDto itemRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id=" + userId + " не существует"));
        ItemRequest itemRequest = ItemRequestMapper.INSTANCE.toItem(itemRequestDto);
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());
        return ItemRequestMapper.INSTANCE.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public Collection<ItemRequestInfoDto> getAllByUserId(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id=" + userId + " не существует"));
        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorId(userId);

        return ItemRequestMapper.INSTANCE.toWithItemsDto(requests).stream()
                .sorted(Comparator.comparing(ItemRequestInfoDto::getCreated).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemRequestDto> getAllOtherUsers(long userId, int from, int size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id=" + userId + " не существует"));
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        return itemRequestRepository.findAllByOrderByCreatedDesc(page).stream()
                .map(ItemRequestMapper.INSTANCE::toItemRequestDto).collect(Collectors.toList());
    }

    @Override
    public ItemRequestInfoDto getById(long userId, long requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id=" + userId + " не существует"));
        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запроса с id=" + userId + " не существует"));
        Collection<ItemForRequestDto> items = itemRepository.findAllByRequestId(requestId).stream()
                .map(ItemMapper.INSTANCE::toItemForRequestDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return ItemRequestMapper.INSTANCE.toItemRequestInfoDto(request, items);
    }
}
