package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingDto;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.GlobalExceptionHandler;
import ru.practicum.shareit.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
@Import(GlobalExceptionHandler.class)
class BookingControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper mapper;
    @MockBean private BookingService bookingService;

    private BookingDto booking() {
        BookingDto d = new BookingDto();
        d.setId(1L);
        d.setStatus(BookingStatus.WAITING);
        d.setStart(LocalDateTime.now().plusDays(1));
        d.setEnd(LocalDateTime.now().plusDays(2));
        return d;
    }

    @Test
    void postBooking_returns() throws Exception {
        when(bookingService.postBooking(eq(1L), any())).thenReturn(booking());

        BookingDto req = new BookingDto();
        req.setItemId(1L);
        req.setStart(LocalDateTime.now().plusDays(1));
        req.setEnd(LocalDateTime.now().plusDays(2));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void patchBooking_returns() throws Exception {
        when(bookingService.patchBooking(eq(1L), eq(true), eq(2L))).thenReturn(booking());

        mockMvc.perform(patch("/bookings/1")
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isOk());
    }

    @Test
    void getBookingById_returns() throws Exception {
        when(bookingService.getBookingById(1L, 1L)).thenReturn(booking());

        mockMvc.perform(get("/bookings/1").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
    }

    @Test
    void getBookingById_notFound_returns404() throws Exception {
        when(bookingService.getBookingById(999L, 1L)).thenThrow(new NoSuchElementException("nf"));

        mockMvc.perform(get("/bookings/999").header("X-Sharer-User-Id", 1))
                .andExpect(status().isNotFound());
    }

    @Test
    void getBookingsByUser_returns() throws Exception {
        when(bookingService.getBookingsByUser(eq("ALL"), eq(1L))).thenReturn(List.of(booking()));

        mockMvc.perform(get("/bookings")
                        .param("state", "ALL")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getBookingsByUser_unknownState_returns400() throws Exception {
        when(bookingService.getBookingsByUser(eq("UNKNOWN"), eq(1L)))
                .thenThrow(new ValidationException("Unknown state"));

        mockMvc.perform(get("/bookings")
                        .param("state", "UNKNOWN")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBookingsByOwner_returns() throws Exception {
        when(bookingService.getBookingsByOwner(eq("ALL"), eq(1L))).thenReturn(List.of(booking()));

        mockMvc.perform(get("/bookings/owner")
                        .param("state", "ALL")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
    }
}

