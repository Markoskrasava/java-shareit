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
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(GlobalExceptionHandler.class)
class UserControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper mapper;
    @MockBean private UserService userService;

    @Test
    void createUser_returnsDto() throws Exception {
        when(userService.createUser(any())).thenReturn(new UserDto(1L, "Иван", "ivan@test.com"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new UserDto(null, "Иван", "ivan@test.com"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("ivan@test.com"));
    }

    @Test
    void createUser_duplicate_returns409() throws Exception {
        when(userService.createUser(any())).thenThrow(new IllegalArgumentException("dup"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new UserDto(null, "X", "dup@test.com"))))
                .andExpect(status().isConflict());
    }

    @Test
    void updateUser_returnsDto() throws Exception {
        when(userService.updateUser(eq(1L), any())).thenReturn(new UserDto(1L, "Новое", "new@test.com"));

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new UserDto(null, "Новое", "new@test.com"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Новое"));
    }

    @Test
    void updateUser_notFound_returns404() throws Exception {
        when(userService.updateUser(eq(999L), any())).thenThrow(new NoSuchElementException("nf"));

        mockMvc.perform(patch("/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new UserDto(null, "X", "x@test.com"))))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUserById_returnsDto() throws Exception {
        when(userService.getUserById(1L)).thenReturn(new UserDto(1L, "Иван", "i@test.com"));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getUserById_notFound_returns404() throws Exception {
        when(userService.getUserById(999L)).thenThrow(new NoSuchElementException("nf"));

        mockMvc.perform(get("/users/999")).andExpect(status().isNotFound());
    }

    @Test
    void getAllUsers_returnsList() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(
                new UserDto(1L, "A", "a@test.com"),
                new UserDto(2L, "B", "b@test.com")));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void deleteUser_returnsOk() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/users/1")).andExpect(status().isOk());
    }

    @Test
    void deleteUser_notFound_returns404() throws Exception {
        doThrow(new NoSuchElementException("nf")).when(userService).deleteUser(999L);

        mockMvc.perform(delete("/users/999")).andExpect(status().isNotFound());
    }
}

