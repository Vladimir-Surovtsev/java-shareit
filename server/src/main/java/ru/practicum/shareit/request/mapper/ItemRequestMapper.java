package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collection;
import java.util.List;

@Mapper
public interface ItemRequestMapper {
    ItemRequestMapper INSTANCE = Mappers.getMapper(ItemRequestMapper.class);

    ItemRequestDto toItemRequestDto(ItemRequest itemRequest);

    ItemRequest toItem(ItemRequestDto itemRequestDto);

    List<ItemRequestInfoDto> toWithItemsDto(List<ItemRequest> itemRequests);

    default ItemRequestInfoDto toItemRequestInfoDto(ItemRequest itemRequest,
                                                    Collection<ItemForRequestDto> itemsForRequest) {
        return ItemRequestInfoDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(itemsForRequest)
                .build();
    }
}
