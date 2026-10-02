package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Item {
    private Long id;

    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @NotNull(message = "Поле available обязательно")
    private Boolean available;

    @NotBlank(message = "Описание не может быть пустым")
    private String description;
    private User owner;
    private ItemRequest itemRequest;
}
