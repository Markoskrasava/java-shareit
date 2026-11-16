package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.GlobalExceptionHandler;
import ru.practicum.shareit.request.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
@Import(GlobalExceptionHandler.class)
class ItemRequestControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper mapper;
    @MockBean private ItemRequestServiceImpl itemRequestServiceImpl;

    private ItemRequestDto dto() {
        ItemRequestDto d = new ItemRequestDto();
        d.setId(1L);
        d.setDescription("Нужна дрель");
        d.setCreated(LocalDateTime.now());
        return d;
    }

    @Test
    void postRequest_returns() throws Exception {
        when(itemRequestServiceImpl.postRequest(any(), eq(1L))).thenReturn(dto());

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new NewItemRequest("Нужна дрель"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Нужна дрель"));
    }

    @Test
    void getRequestsByUser_returns() throws Exception {
        when(itemRequestServiceImpl.getRequestsByUser(1L)).thenReturn(List.of(dto()));

        mockMvc.perform(get("/requests").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getRequestsByOther_returns() throws Exception {
        when(itemRequestServiceImpl.getRequestsByOther(1L)).thenReturn(List.of(dto()));

        mockMvc.perform(get("/requests/all").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
    }

    @Test
    void getRequestById_returns() throws Exception {
        when(itemRequestServiceImpl.getRequestById(1L)).thenReturn(dto());

        mockMvc.perform(get("/requests/1")).andExpect(status().isOk());
    }

    @Test
    void getRequestById_notFound_returns404() throws Exception {
        when(itemRequestServiceImpl.getRequestById(999L)).thenThrow(new NoSuchElementException("nf"));

        mockMvc.perform(get("/requests/999")).andExpect(status().isNotFound());
    }
}
