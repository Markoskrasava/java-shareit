package ru.practicum.shareit.item;

public class ItemShortMapper {

    private ItemShortMapper() {
    }

    public static ItemShortDto toShortDto(Item item) {
        if (item == null) {
            return null;
        }
        ItemShortDto dto = new ItemShortDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setOwnerId(item.getOwner().getId());   // ← обязательно
        return dto;
    }
}
