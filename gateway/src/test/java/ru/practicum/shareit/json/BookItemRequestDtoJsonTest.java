package ru.practicum.shareit.json;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<BookItemRequestDto> json;

    @Test
    void shouldDeserializeIsoDates() throws Exception {
        String content = "{\"start\":\"2030-01-01T10:00:00\",\"end\":\"2030-01-02T12:30:00\",\"itemId\":5}";

        BookItemRequestDto dto = json.parseObject(content);

        assertThat(dto.getStart()).isEqualTo(LocalDateTime.of(2030, 1, 1, 10, 0));
        assertThat(dto.getEnd()).isEqualTo(LocalDateTime.of(2030, 1, 2, 12, 30));
        assertThat(dto.getItemId()).isEqualTo(5L);
    }

    @Test
    void shouldSerializeDatesInIsoFormat() throws Exception {
        BookItemRequestDto dto = new BookItemRequestDto();
        dto.setItemId(5L);
        dto.setStart(LocalDateTime.of(2030, 1, 1, 10, 0));
        dto.setEnd(LocalDateTime.of(2030, 1, 2, 12, 30));

        assertThat(json.write(dto))
                .hasJsonPathStringValue("$.start", "2030-01-01T10:00:00")
                .hasJsonPathStringValue("$.end", "2030-01-02T12:30:00")
                .hasJsonPathNumberValue("$.itemId", 5);
    }

    @Test
    void shouldDeserializeWithoutId() throws Exception {
        String content = "{\"start\":\"2030-01-01T10:00:00\",\"end\":\"2030-01-02T12:30:00\"}";

        BookItemRequestDto dto = json.parseObject(content);

        assertThat(dto.getStart()).isEqualTo(LocalDateTime.of(2030, 1, 1, 10, 0));
        assertThat(dto.getEnd()).isEqualTo(LocalDateTime.of(2030, 1, 2, 12, 30));
        assertThat(dto.getItemId()).isNull();
    }
}
