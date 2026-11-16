package ru.practicum.shareit.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @Test
    void createUser_shouldSaveAndReturnDto() {
        UserDto created = userService.createUser(new UserDto(null, "Иван", "ivan@test.com"));

        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("Иван");
        assertThat(created.getEmail()).isEqualTo("ivan@test.com");
    }

    @Test
    void createUser_withDuplicateEmail_shouldThrowIllegalArgument() {
        userService.createUser(new UserDto(null, "Иван", "dup@test.com"));

        assertThatThrownBy(() -> userService.createUser(new UserDto(null, "Пётр", "dup@test.com")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void updateUser_shouldChangeFields() {
        UserDto created = userService.createUser(new UserDto(null, "Иван", "upd@test.com"));

        UserDto updated = userService.updateUser(created.getId(),
                new UserDto(null, "Пётр", "new@test.com"));

        assertThat(updated.getName()).isEqualTo("Пётр");
        assertThat(updated.getEmail()).isEqualTo("new@test.com");
    }

    @Test
    void updateUser_withDuplicateEmail_shouldThrowIllegalArgument() {
        userService.createUser(new UserDto(null, "A", "a@test.com"));
        UserDto b = userService.createUser(new UserDto(null, "B", "b@test.com"));

        assertThatThrownBy(() -> userService.updateUser(b.getId(),
                new UserDto(null, null, "a@test.com")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void updateUser_notFound_shouldThrow() {
        assertThatThrownBy(() -> userService.updateUser(999L,
                new UserDto(null, "X", "x@test.com")))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void getUserById_shouldReturn() {
        UserDto created = userService.createUser(new UserDto(null, "Иван", "get@test.com"));

        UserDto found = userService.getUserById(created.getId());

        assertThat(found.getEmail()).isEqualTo("get@test.com");
    }

    @Test
    void getUserById_notFound_shouldThrow() {
        assertThatThrownBy(() -> userService.getUserById(999L))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void getAllUsers_shouldReturnAll() {
        userService.createUser(new UserDto(null, "A", "1@test.com"));
        userService.createUser(new UserDto(null, "B", "2@test.com"));

        List<UserDto> all = userService.getAllUsers();

        assertThat(all).hasSize(2);
    }

    @Test
    void deleteUser_shouldRemove() {
        UserDto created = userService.createUser(new UserDto(null, "Иван", "del@test.com"));

        userService.deleteUser(created.getId());

        assertThatThrownBy(() -> userService.getUserById(created.getId()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void deleteUser_notFound_shouldThrow() {
        assertThatThrownBy(() -> userService.deleteUser(999L))
                .isInstanceOf(NoSuchElementException.class);
    }
}

