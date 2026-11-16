package ru.practicum.shareit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class ItemServiceImplTest {

    @Autowired private ItemService itemService;
    @Autowired private UserService userService;

    private Long ownerId;
    private Long otherId;

    @BeforeEach
    void setUp() {
        ownerId = userService.createUser(new UserDto(null, "Owner", "owner@test.com")).getId();
        otherId = userService.createUser(new UserDto(null, "Other", "other@test.com")).getId();
    }

    private ItemDto newItem(String name, String desc, Boolean available) {
        return new ItemDto(null, name, desc, available, null, null, null, null);
    }

    @Test
    void addItem_shouldSave() {
        ItemDto item = itemService.addItem(newItem("Дрель", "Мощная", true), ownerId);

        assertThat(item.getId()).isNotNull();
        assertThat(item.getName()).isEqualTo("Дрель");
    }

    @Test
    void addItem_userNotFound_shouldThrow() {
        assertThatThrownBy(() -> itemService.addItem(newItem("X", "Y", true), 999L))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void patchItem_shouldUpdateFields() {
        ItemDto item = itemService.addItem(newItem("Дрель", "Мощная", true), ownerId);

        ItemDto patch = new ItemDto(null, "Перфоратор", null, null, null, null, null, null);
        ItemDto updated = itemService.patchItem(item.getId(), patch, ownerId);

        assertThat(updated.getName()).isEqualTo("Перфоратор");
        assertThat(updated.getDescription()).isEqualTo("Мощная");
    }

    @Test
    void patchItem_notOwner_shouldThrow() {
        ItemDto item = itemService.addItem(newItem("Дрель", "Мощная", true), ownerId);
        ItemDto patch = new ItemDto(null, "Хак", null, null, null, null, null, null);

        assertThatThrownBy(() -> itemService.patchItem(item.getId(), patch, otherId))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void getItemById_shouldReturn() {
        ItemDto item = itemService.addItem(newItem("Дрель", "Мощная", true), ownerId);

        assertThat(itemService.getItemById(item.getId()).getName()).isEqualTo("Дрель");
    }

    @Test
    void getItemById_notFound_shouldThrow() {
        assertThatThrownBy(() -> itemService.getItemById(999L))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void getAllItems_shouldReturnOnlyOwners() {
        itemService.addItem(newItem("A", "desc A", true), ownerId);
        itemService.addItem(newItem("B", "desc B", true), ownerId);
        itemService.addItem(newItem("C", "desc C", true), otherId);

        List<ItemDto> result = itemService.getAllItems(ownerId);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(ItemDto::getName).containsExactlyInAnyOrder("A", "B");
    }

    @Test
    void searchItems_byName() {
        itemService.addItem(newItem("Дрель", "Мощная", true), ownerId);
        itemService.addItem(newItem("Молоток", "Тяжёлый", true), ownerId);

        List<ItemDto> result = itemService.searchItems("дрель");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Дрель");
    }

    @Test
    void searchItems_byDescription() {
        itemService.addItem(newItem("Дрель", "Мощная штука", true), ownerId);

        assertThat(itemService.searchItems("мощная")).hasSize(1);
    }

    @Test
    void searchItems_emptyText_shouldReturnEmpty() {
        itemService.addItem(newItem("Дрель", "Мощная", true), ownerId);

        assertThat(itemService.searchItems("")).isEmpty();
        assertThat(itemService.searchItems("   ")).isEmpty();
    }

    @Test
    void searchItems_skipUnavailable() {
        itemService.addItem(newItem("Дрель", "Мощная", false), ownerId);

        assertThat(itemService.searchItems("дрель")).isEmpty();
    }
}
