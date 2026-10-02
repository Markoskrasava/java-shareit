package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

@Data
@NoArgsConstructor
public class Item {
    private Long id;
    private String name;
    private Boolean available;
    private String description;
    private User owner;
    private ItemRequest itemRequest;

    public Item(Long id, @NotBlank(message = "Название не может быть пустым") String name, @NotBlank(message = "Описание не может быть пустым") String description,
                @NotNull(message = "Поле available обязательно") Boolean available, User owner, ItemRequest itemRequest) {
    }
}
