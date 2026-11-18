package ru.practicum.shareit.json;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.BookingDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void shouldSerializeDatesInIsoFormat() throws Exception {
        BookingDto dto = new BookingDto();
        dto.setStart(LocalDateTime.of(2030, 1, 1, 10, 0, 0));
        dto.setEnd(LocalDateTime.of(2030, 1, 2, 12, 30, 0));

        assertThat(json.write(dto))
                .hasJsonPathStringValue("$.start", "2030-01-01T10:00:00")
                .hasJsonPathStringValue("$.end", "2030-01-02T12:30:00");
    }

    @Test
    void shouldDeserializeDates() throws Exception {
        String content = "{\"start\":\"2030-01-01T10:00:00\",\"end\":\"2030-01-02T12:30:00\",\"itemId\":1}";

        BookingDto dto = json.parseObject(content);

        assertThat(dto.getStart()).isEqualTo(LocalDateTime.of(2030, 1, 1, 10, 0));
        assertThat(dto.getEnd()).isEqualTo(LocalDateTime.of(2030, 1, 2, 12, 30));
    }
}
