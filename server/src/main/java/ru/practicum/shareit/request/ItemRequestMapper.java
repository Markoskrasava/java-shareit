package ru.practicum.shareit.request;

import ru.practicum.shareit.item.ItemShortDto;

import java.util.List;

public class ItemRequestMapper {

    private ItemRequestMapper() {
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest request, List<ItemShortDto> items) {
        if (request == null) {
            return null;
        }
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(request.getId());
        dto.setDescription(request.getDescription());
        dto.setCreated(request.getCreated());
        dto.setItems(items != null ? items : List.of());
        return dto;
    }
}
