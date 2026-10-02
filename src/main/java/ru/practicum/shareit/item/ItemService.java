package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto addItem(ItemDto itemDto, Long userId);

    ItemDto patchItem(Long itemId, ItemDto patchDto, Long userId);

    ItemDto getItemById(Long itemId);

    List<ItemDto> getAllItems(Long userId);

    List<ItemDto> searchItems(String text);
}
