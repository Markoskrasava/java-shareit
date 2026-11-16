package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemShortDto {
    private Long id;
    private String name;
    private Long ownerId;

    public ItemShortDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
