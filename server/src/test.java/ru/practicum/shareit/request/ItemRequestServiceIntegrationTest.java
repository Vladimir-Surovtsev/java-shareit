package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceIntegrationTest {
    final ItemRequestServiceImpl itemRequestService;
    final ItemRequestRepository itemRequestRepository;
    final UserRepository userRepository;
    User user;

    @BeforeEach
    void setUp() {
        user = userRepository.save(User.builder()
                .name("Test User")
                .email("test@example.com")
                .build());
    }

    @Test
    void testCreateRequest() {
        ItemRequestDto requestDto = ItemRequestDto.builder()
                .description("Request description")
                .created(LocalDateTime.now())
                .build();
        ItemRequestDto savedRequest = itemRequestService.create(user.getId(), requestDto);
        Optional<ItemRequest> foundRequest = itemRequestRepository.findById(savedRequest.getId());
        assertThat(foundRequest).isPresent();
        assertThat(foundRequest.get().getDescription()).isEqualTo("Request description");
    }

    @Test
    void createItemRequest_UserNotFound_ShouldThrowNotFoundException() {
        long invalidUserId = 999L;
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description("Need a drill")
                .build();
        assertThrows(NotFoundException.class, () -> itemRequestService.create(invalidUserId, itemRequestDto));
    }

    @Test
    void testGetAllRequestsByUserId() {
        itemRequestRepository.save(ItemRequest.builder()
                .description("Request description")
                .created(LocalDateTime.now())
                .requestor(user)
                .build());
        Collection<ItemRequestInfoDto> requests = itemRequestService.getAllByUserId(user.getId());
        assertThat(requests).hasSize(1);
    }

    @Test
    void getAllByUserId_UserNotFound_ShouldThrowNotFoundException() {
        long invalidUserId = 999L;
        assertThrows(NotFoundException.class, () -> itemRequestService.getAllByUserId(invalidUserId));
    }

    @Test
    void testGetAllRequestsOtherUsers() {
        itemRequestRepository.save(ItemRequest.builder()
                .description("Request description")
                .created(LocalDateTime.now())
                .requestor(user)
                .build());
        Collection<ItemRequestDto> requests = itemRequestService.getAllOtherUsers(user.getId(), 0, 10);
        assertThat(requests).hasSize(1);
    }

    @Test
    void getAllOtherUsers_UserNotFound_ShouldThrowNotFoundException() {
        long invalidUserId = 999L;
        assertThrows(NotFoundException.class, () -> itemRequestService.getAllOtherUsers(invalidUserId, 0, 10));
    }

    @Test
    void testGetRequestById() {
        ItemRequest itemRequest = itemRequestRepository.save(ItemRequest.builder()
                .description("Request description")
                .created(LocalDateTime.now())
                .requestor(user)
                .build());
        ItemRequestInfoDto requestInfo = itemRequestService.getById(user.getId(), itemRequest.getId());
        assertThat(requestInfo.getId()).isEqualTo(itemRequest.getId());
        assertThat(requestInfo.getDescription()).isEqualTo("Request description");
    }

    @Test
    void getById_UserNotFound_ShouldThrowNotFoundException() {
        long invalidUserId = 999L;
        long requestId = 1L;
        assertThrows(NotFoundException.class, () -> itemRequestService.getById(invalidUserId, requestId));
    }
}
