package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper mapper;
    @MockBean private BookingClient bookingClient;

    private BookItemRequestDto future() {
        return new BookItemRequestDto(1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));
    }

    @Test
    void postBooking_delegates() throws Exception {
        when(bookingClient.bookItem(eq(1L), any()))
                .thenReturn(ResponseEntity.ok(Map.of("id", 1, "status", "WAITING")));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(future())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void postBooking_endBeforeStart_returns400() throws Exception {
        BookItemRequestDto bad = new BookItemRequestDto(1L,
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(1));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bad)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingClient);
    }

    @Test
    void postBooking_endEqualsStart_returns400() throws Exception {
        LocalDateTime same = LocalDateTime.now().plusDays(1);
        BookItemRequestDto bad = new BookItemRequestDto(1L, same, same);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bad)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingClient);
    }

    @Test
    void postBooking_pastDate_returns400() throws Exception {
        BookItemRequestDto bad = new BookItemRequestDto(1L,
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bad)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingClient);
    }

    @Test
    void postBooking_missingItemId_returns400() throws Exception {
        BookItemRequestDto bad = new BookItemRequestDto();

        bad.setStart(LocalDateTime.now().plusDays(1));
        bad.setEnd(LocalDateTime.now().plusDays(2));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bad)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingClient);
    }

    @Test
    void patchBooking_delegates() throws Exception {
        when(bookingClient.approveBooking(2L, 1L, true))
                .thenReturn(ResponseEntity.ok(Map.of("id", 1)));

        mockMvc.perform(patch("/bookings/1")
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isOk());
    }

    @Test
    void patchBooking_withoutApproved_returns400() throws Exception {
        mockMvc.perform(patch("/bookings/1").header("X-Sharer-User-Id", 2))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingClient);
    }

    @Test
    void getBookingById_delegates() throws Exception {
        when(bookingClient.getBooking(1L, 1L)).thenReturn(ResponseEntity.ok(Map.of("id", 1)));

        mockMvc.perform(get("/bookings/1").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
    }

    @Test
    void getBookings_defaultStateAll() throws Exception {
        when(bookingClient.getBookings(eq(1L), any(), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok(List.of()));

        mockMvc.perform(get("/bookings").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
    }

    @Test
    void getBookings_unknownState_returns400() throws Exception {
        mockMvc.perform(get("/bookings")
                        .param("state", "UNKNOWN")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingClient);
    }

    @Test
    void getBookingsByOwner_delegates() throws Exception {
        when(bookingClient.getBookingsByOwner(eq(1L), any(), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok(List.of()));

        mockMvc.perform(get("/bookings/owner").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
    }

    @Test
    void getBookingsByOwner_unknownState_returns400() throws Exception {
        mockMvc.perform(get("/bookings/owner")
                        .param("state", "UNKNOWN")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingClient);
    }
}
