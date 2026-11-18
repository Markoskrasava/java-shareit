package ru.practicum.shareit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingDto;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class BookingServiceImplTest {

    @Autowired private BookingService bookingService;
    @Autowired private UserService userService;
    @Autowired private ItemService itemService;

    private Long ownerId;
    private Long bookerId;
    private Long itemId;

    @BeforeEach
    void setUp() {
        ownerId = userService.createUser(new UserDto(null, "Owner", "o@test.com")).getId();
        bookerId = userService.createUser(new UserDto(null, "Booker", "b@test.com")).getId();
        ItemDto item = itemService.addItem(
                new ItemDto(null, "Дрель", "Мощная", true, null, null, null, null), ownerId);
        itemId = item.getId();
    }

    private BookingDto dto(LocalDateTime start, LocalDateTime end) {
        BookingDto d = new BookingDto();
        d.setStart(start);
        d.setEnd(end);
        d.setItemId(itemId);
        return d;
    }

    @Test
    void postBooking_shouldCreateWaiting() {
        BookingDto booking = bookingService.postBooking(bookerId,
                dto(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)));

        assertThat(booking.getId()).isNotNull();
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void postBooking_endEqualsStart_shouldThrow() {
        LocalDateTime same = LocalDateTime.now().plusDays(1);

        assertThatThrownBy(() -> bookingService.postBooking(bookerId, dto(same, same)))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void postBooking_itemUnavailable_shouldThrow() {
        ItemDto unavail = itemService.addItem(
                new ItemDto(null, "X", "Y", false, null, null, null, null), ownerId);
        BookingDto d = dto(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        d.setItemId(unavail.getId());

        assertThatThrownBy(() -> bookingService.postBooking(bookerId, d))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void postBooking_ownItem_shouldThrow() {
        assertThatThrownBy(() -> bookingService.postBooking(ownerId,
                dto(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2))))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void postBooking_userNotFound_shouldThrow() {
        assertThatThrownBy(() -> bookingService.postBooking(999L,
                dto(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2))))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void patchBooking_approve() {
        BookingDto created = bookingService.postBooking(bookerId,
                dto(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)));

        BookingDto approved = bookingService.patchBooking(created.getId(), true, ownerId);

        assertThat(approved.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void patchBooking_reject() {
        BookingDto created = bookingService.postBooking(bookerId,
                dto(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)));

        BookingDto rejected = bookingService.patchBooking(created.getId(), false, ownerId);

        assertThat(rejected.getStatus()).isEqualTo(BookingStatus.REJECTED);
    }

    @Test
    void patchBooking_notOwner_shouldThrow() {
        BookingDto created = bookingService.postBooking(bookerId,
                dto(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)));

        assertThatThrownBy(() -> bookingService.patchBooking(created.getId(), true, bookerId))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void patchBooking_alreadyProcessed_shouldThrow() {
        BookingDto created = bookingService.postBooking(bookerId,
                dto(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)));
        bookingService.patchBooking(created.getId(), true, ownerId);

        assertThatThrownBy(() -> bookingService.patchBooking(created.getId(), false, ownerId))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void getBookingById_ownerCanView() {
        BookingDto created = bookingService.postBooking(bookerId,
                dto(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)));

        assertThat(bookingService.getBookingById(created.getId(), ownerId)).isNotNull();
    }

    @Test
    void getBookingById_bookerCanView() {
        BookingDto created = bookingService.postBooking(bookerId,
                dto(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)));

        assertThat(bookingService.getBookingById(created.getId(), bookerId)).isNotNull();
    }

    @Test
    void getBookingById_otherUser_shouldThrow() {
        Long third = userService.createUser(new UserDto(null, "Third", "t@test.com")).getId();
        BookingDto created = bookingService.postBooking(bookerId,
                dto(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)));

        assertThatThrownBy(() -> bookingService.getBookingById(created.getId(), third))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void getBookingsByUser_all() {
        bookingService.postBooking(bookerId,
                dto(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)));

        List<BookingDto> result = bookingService.getBookingsByUser("ALL", bookerId);

        assertThat(result).hasSize(1);
    }

    @Test
    void getBookingsByUser_unknownState_shouldThrow() {
        assertThatThrownBy(() -> bookingService.getBookingsByUser("UNKNOWN", bookerId))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void getBookingsByUser_userNotFound_shouldThrow() {
        assertThatThrownBy(() -> bookingService.getBookingsByUser("ALL", 999L))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void getBookingsByOwner_all() {
        bookingService.postBooking(bookerId,
                dto(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)));

        List<BookingDto> result = bookingService.getBookingsByOwner("ALL", ownerId);

        assertThat(result).hasSize(1);
    }
}
