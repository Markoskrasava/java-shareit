package ru.practicum.shareit.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class User {
    private Long id;

    @NotBlank(message = "Email не может быть пустым")
    private String email;

    private String name;
}
