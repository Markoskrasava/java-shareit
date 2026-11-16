package ru.practicum.shareit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.ItemRequestDto;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.NewItemRequest;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class ItemRequestServiceImplTest {

    @Autowired private ItemRequestService requestService;
    @Autowired private UserService userService;

    private Long userId;
    private Long otherId;

    @BeforeEach
    void setUp() {
        userId = userService.createUser(new UserDto(null, "A", "a@test.com")).getId();
        otherId = userService.createUser(new UserDto(null, "B", "b@test.com")).getId();
    }

    @Test
    void postRequest_shouldSave() {
        ItemRequestDto created = requestService.postRequest(new NewItemRequest("Нужна дрель"), userId);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getDescription()).isEqualTo("Нужна дрель");
        assertThat(created.getCreated()).isNotNull();
    }

    @Test
    void postRequest_userNotFound_shouldThrow() {
        assertThatThrownBy(() -> requestService.postRequest(new NewItemRequest("X"), 999L))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void getRequestsByUser_shouldReturnOwn() {
        requestService.postRequest(new NewItemRequest("A"), userId);
        requestService.postRequest(new NewItemRequest("B"), userId);
        requestService.postRequest(new NewItemRequest("C"), otherId);

        List<ItemRequestDto> result = requestService.getRequestsByUser(userId);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(ItemRequestDto::getDescription)
                .containsExactlyInAnyOrder("A", "B");
    }

    @Test
    void getRequestsByOther_shouldExcludeOwn() {
        requestService.postRequest(new NewItemRequest("A"), userId);
        requestService.postRequest(new NewItemRequest("B"), otherId);

        List<ItemRequestDto> result = requestService.getRequestsByOther(userId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDescription()).isEqualTo("B");
    }

    @Test
    void getRequestById_shouldReturn() {
        ItemRequestDto created = requestService.postRequest(new NewItemRequest("Дрель"), userId);

        ItemRequestDto found = requestService.getRequestById(created.getId());

        assertThat(found.getDescription()).isEqualTo("Дрель");
    }

    @Test
    void getRequestById_notFound_shouldThrow() {
        assertThatThrownBy(() -> requestService.getRequestById(999L))
                .isInstanceOf(NoSuchElementException.class);
    }
}
