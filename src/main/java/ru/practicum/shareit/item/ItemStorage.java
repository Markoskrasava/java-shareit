package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();
    private Long newId = 1L;

    public Item addItem(Item item) {
        item.setId(newId);
        items.put(newId, item);
        newId++;
        return item;
    }

    public Item patchItem(Item item) {
        if (!items.containsKey(item.getId())) {
            throw new NoSuchElementException("Вещь не найдена");
        }

        items.put(item.getId(), item);
        return item;
    }

    public Item getItemById(Long itemId) {
        if (itemId == null) {
            throw new NoSuchElementException("Вещь не найдена");
        }
        return items.get(itemId);
    }

    public List<Item> getAllItems(Long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }

    public List<Item> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        String lower = text.toLowerCase();
        return items.values().stream()
                .filter(item -> Boolean.TRUE.equals(item.getAvailable()))
                .filter(item -> {
                    String name = item.getName() != null ? item.getName().toLowerCase() : "";
                    String desc = item.getDescription() != null ? item.getDescription().toLowerCase() : "";
                    return name.contains(lower) || desc.contains(lower);
                })
                .collect(Collectors.toList());
    }
}
