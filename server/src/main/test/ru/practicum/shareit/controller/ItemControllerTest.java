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
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.comment.CommentDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
@Import(GlobalExceptionHandler.class)
class ItemControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper mapper;
    @MockBean private ItemService itemService;

    private ItemDto item() {
        return new ItemDto(1L, "Дрель", "Мощная", true, null, null, null, null);
    }

    @Test
    void addItem_returnsDto() throws Exception {
        when(itemService.addItem(any(), eq(1L))).thenReturn(item());

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(
                                new ItemDto(null, "Дрель", "Мощная", true, null, null, null, null))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Дрель"));
    }

    @Test
    void patchItem_returnsDto() throws Exception {
        when(itemService.patchItem(eq(1L), any(), eq(1L))).thenReturn(item());

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(
                                new ItemDto(null, "Дрель", null, null, null, null, null, null))))
                .andExpect(status().isOk());
    }

    @Test
    void getItemById_returns() throws Exception {
        when(itemService.getItemById(1L)).thenReturn(item());

        mockMvc.perform(get("/items/1").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getItemById_notFound_returns404() throws Exception {
        when(itemService.getItemById(999L)).thenThrow(new NoSuchElementException("nf"));

        mockMvc.perform(get("/items/999").header("X-Sharer-User-Id", 1))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllItems_returnsList() throws Exception {
        when(itemService.getAllItems(1L)).thenReturn(List.of(item()));

        mockMvc.perform(get("/items").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void searchItems_byText() throws Exception {
        when(itemService.searchItems("дрель")).thenReturn(List.of(item()));

        mockMvc.perform(get("/items/search")
                        .param("text", "дрель")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void addComment_returnsDto() throws Exception {
        CommentDto response = new CommentDto();
        response.setId(1L);
        response.setText("Отлично");
        response.setAuthorName("Иван");
        response.setCreated(LocalDateTime.now());

        when(itemService.addComment(eq(1L), any(), eq(1L))).thenReturn(response);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(response)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Отлично"));
    }
}
