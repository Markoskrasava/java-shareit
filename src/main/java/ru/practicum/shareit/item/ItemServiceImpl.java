package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto addItem(ItemDto itemDto, Long userId) {
        User owner = userStorage.getUserById(userId);
        if (owner == null) {
            throw new NoSuchElementException("Пользователь с ID " + userId + " не найден");
        }

        Item item = ItemMapper.toItem(itemDto, owner);

        Item saved = itemStorage.addItem(item);

        return ItemMapper.toItemDto(saved);
    }

    @Override
    public ItemDto patchItem(Long itemId, ItemDto patchDto, Long userId) {
        Item existing = itemStorage.getItemById(itemId);
        if (existing == null) {
            throw new NoSuchElementException("Вещь не найдена");
        }

        if (!existing.getOwner().getId().equals(userId)) {
            throw new NoSuchElementException("Только владелец может обновлять вещь");
        }

        if (patchDto.getName() != null) {
            existing.setName(patchDto.getName());
        }
        if (patchDto.getDescription() != null) {
            existing.setDescription(patchDto.getDescription());
        }
        if (patchDto.getAvailable() != null) {
            existing.setAvailable(patchDto.getAvailable());
        }

        Item updated = itemStorage.patchItem(existing);

        return ItemMapper.toItemDto(updated);
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        Item item = itemStorage.getItemById(itemId);
        if (item == null) {
            throw new NoSuchElementException("Вещь не найдена");
        }
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getAllItems(Long userId) {
        List<Item> items = itemStorage.getAllItems(userId);
        return items.stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        List<Item> items = itemStorage.searchItems(text);
        return items.stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }
}
