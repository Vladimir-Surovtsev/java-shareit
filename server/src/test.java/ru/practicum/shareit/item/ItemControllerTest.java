package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemControllerTest {
    private final MockMvc mockMvc;
    @MockBean
    private ItemService itemService;
    private final ObjectMapper objectMapper;
    private ItemDto itemDto;
    private ItemInfoDto itemInfoDto;

    @BeforeEach
    void setUp() {
        itemDto = ItemDto.builder()
                .id(1L)
                .name("Item")
                .description("Description")
                .available(true)
                .build();
        itemInfoDto = ItemInfoDto.builder()
                .id(1L)
                .name("Item")
                .description("Description")
                .available(true)
                .comments(Collections.emptyList())
                .build();
    }

    @Test
    void create_ShouldReturnCreatedItem() throws Exception {
        ItemCreateDto itemCreateDto = ItemCreateDto.builder()
                .name("New Item")
                .description("New Description")
                .available(true)
                .build();
        Mockito.when(itemService.create(eq(1L), any(ItemCreateDto.class))).thenReturn(itemDto);
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemCreateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()));
    }

    @Test
    void create_ShouldThrowNotFoundException_WhenUserNotFound() throws Exception {
        ItemCreateDto itemCreateDto = ItemCreateDto.builder()
                .name("New Item")
                .description("New Description")
                .available(true)
                .build();
        Mockito.when(itemService.create(eq(1L), any(ItemCreateDto.class)))
                .thenThrow(new NotFoundException("User not found"));
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemCreateDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_ShouldReturnUpdatedItem_WhenDataIsValid() throws Exception {
        ItemUpdateDto itemUpdateDto = ItemUpdateDto.builder()
                .name("Updated Item")
                .description("Updated Description")
                .available(true)
                .build();
        Mockito.when(itemService.update(eq(1L), eq(1L), any(ItemUpdateDto.class))).thenReturn(itemDto);
        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()));
    }

    @Test
    void update_ShouldReturnNotFound_WhenItemNotFound() throws Exception {
        ItemUpdateDto itemUpdateDto = ItemUpdateDto.builder()
                .name("Updated Item")
                .description("Updated Description")
                .available(true)
                .build();
        Mockito.when(itemService.update(eq(1L), eq(1L), any(ItemUpdateDto.class)))
                .thenThrow(new NotFoundException("Item not found"));
        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemUpdateDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getById_ShouldReturnItemInfo() throws Exception {
        Mockito.when(itemService.getById(eq(1L), eq(1L))).thenReturn(itemInfoDto);
        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemInfoDto.getId()))
                .andExpect(jsonPath("$.name").value(itemInfoDto.getName()));
    }

    @Test
    void getById_ShouldReturnNotFound_WhenItemNotFound() throws Exception {
        Mockito.when(itemService.getById(eq(1L), eq(1L)))
                .thenThrow(new NotFoundException("Item not found"));
        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllByUsersId_ShouldReturnItems() throws Exception {
        Mockito.when(itemService.getAllByUsersId(eq(1L))).thenReturn(Collections.singletonList(itemInfoDto));
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemInfoDto.getId()))
                .andExpect(jsonPath("$[0].name").value(itemInfoDto.getName()));
    }

    @Test
    void delete_ShouldRemoveItem_WhenDataIsValid() throws Exception {
        mockMvc.perform(delete("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
        Mockito.verify(itemService, Mockito.times(1)).delete(1L, 1L);
    }

    @Test
    void delete_ShouldReturnNotFound_WhenItemNotFound() throws Exception {
        Mockito.doThrow(new NotFoundException("Item not found")).when(itemService).delete(1L, 1L);
        mockMvc.perform(delete("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void search_ShouldReturnItems_WhenTextIsValid() throws Exception {
        Mockito.when(itemService.getAllByText("search")).thenReturn(Collections.singletonList(itemDto));
        mockMvc.perform(get("/items/search")
                        .param("text", "search")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemDto.getId()))
                .andExpect(jsonPath("$[0].name").value(itemDto.getName()));
    }

    @Test
    void search_ShouldReturnEmptyList_WhenTextIsEmpty() throws Exception {
        mockMvc.perform(get("/items/search")
                        .param("text", "")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void addComment_ShouldReturnCreatedComment() throws Exception {
        CommentDto commentDto = CommentDto.builder()
                .text("Great Item!")
                .build();
        CommentDto returnedComment = CommentDto.builder()
                .id(1L)
                .text("Great Item!")
                .authorName("Test User")
                .created(LocalDateTime.now())
                .build();
        Mockito.when(itemService.addComment(eq(1L), eq(1L), any(CommentDto.class)))
                .thenReturn(returnedComment);
        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(returnedComment.getId()))
                .andExpect(jsonPath("$.text").value(returnedComment.getText()))
                .andExpect(jsonPath("$.authorName").value(returnedComment.getAuthorName()));
    }
}
