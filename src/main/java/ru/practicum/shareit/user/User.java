package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;

    private String name;

    @Email(message = "Должен присутствовать символ @")
    @NotBlank(message = "Email не может быть пустым")
    private String email;

}
