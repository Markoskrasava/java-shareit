package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.GlobalExceptionHandler;
import ru.practicum.shareit.request.ItemRequestClient;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.ItemRequestDto;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
@Import(GlobalExceptionHandler.class)
class ItemRequestControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper mapper;
    @MockBean private ItemRequestClient requestClient;

    @Test
    void postRequest_delegates() throws Exception {
        when(requestClient.postRequest(eq(1L), any()))
                .thenReturn(ResponseEntity.ok(Map.of("id", 1, "description", "Нужна дрель")));

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new ItemRequestDto("Нужна дрель"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Нужна дрель"));
    }

    @Test
    void postRequest_blankDescription_returns400() throws Exception {
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new ItemRequestDto(""))))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(requestClient);
    }

    @Test
    void postRequest_nullDescription_returns400() throws Exception {
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(requestClient);
    }

    @Test
    void getRequestsByUser_delegates() throws Exception {
        when(requestClient.getRequestsByUser(1L))
                .thenReturn(ResponseEntity.ok(List.of()));

        mockMvc.perform(get("/requests").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getRequestsByOther_delegates() throws Exception {
        when(requestClient.getRequestsByOther(1L))
                .thenReturn(ResponseEntity.ok(List.of()));

        mockMvc.perform(get("/requests/all").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getRequestById_delegates() throws Exception {
        when(requestClient.getRequestById(1L))
                .thenReturn(ResponseEntity.ok(Map.of("id", 1, "description", "Дрель")));

        mockMvc.perform(get("/requests/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }
}

