package ru.practicum.shareit.json;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.request.ItemRequestDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void shouldSerializeCreatedInIsoFormat() throws Exception {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(1L);
        dto.setDescription("Нужна дрель");
        dto.setCreated(LocalDateTime.of(2030, 1, 1, 10, 0, 0));

        assertThat(json.write(dto))
                .hasJsonPathNumberValue("$.id", 1)
                .hasJsonPathStringValue("$.description", "Нужна дрель")
                .hasJsonPathStringValue("$.created", "2030-01-01T10:00:00");
    }

    @Test
    void shouldDeserializeCreated() throws Exception {
        String content = "{\"id\":1,\"description\":\"Нужна дрель\",\"created\":\"2030-01-01T10:00:00\"}";

        ItemRequestDto dto = json.parseObject(content);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getDescription()).isEqualTo("Нужна дрель");
        assertThat(dto.getCreated()).isEqualTo(LocalDateTime.of(2030, 1, 1, 10, 0));
    }
}
