package ru.practicum.shareit.item;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto addItem(ItemDto itemDto, Long userId) {
        User owner = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("Пользователь с ID " + userId + " не найден"));

        Item item = ItemMapper.toItem(itemDto, owner);

        Item saved = itemRepository.save(item);

        return ItemMapper.toItemDto(saved);
    }

    @Override
    @Transactional
    public ItemDto patchItem(Long itemId, ItemDto patchDto, Long userId) {
        Item existing = itemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Вещь не найдена"));

        if (!existing.getOwner().getId().equals(userId)) {
            throw new NoSuchElementException("Вещь не найдена");
        }

        if (patchDto.getName() != null && !patchDto.getName().isBlank()) {
            existing.setName(patchDto.getName());
        }
        if (patchDto.getDescription() != null && !patchDto.getDescription().isBlank()) {
            existing.setDescription(patchDto.getDescription());
        }
        if (patchDto.getAvailable() != null) {
            existing.setAvailable(patchDto.getAvailable());
        }

        return ItemMapper.toItemDto(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto getItemById(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NoSuchElementException("Вещь не найдена"));
        ItemDto itemDto = ItemMapper.toItemDto(item);
        itemDto.setComments(commentRepository.findAllByItemId(itemId).stream()
                .map(CommentMapper::toDto)
                .toList());
        return itemDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getAllItems(Long userId) {
        List<Item> items = itemRepository.getAllByOwnerId(userId);
        LocalDateTime now = LocalDateTime.now();

        Map<Long, BookingShortDto> lastByItem = new HashMap<>();
        for (Booking b : bookingRepository.findPastApprovedByOwner(userId, now)) {
            lastByItem.putIfAbsent(b.getItem().getId(), toShort(b));
        }

        Map<Long, BookingShortDto> nextByItem = new HashMap<>();
        for (Booking b : bookingRepository.findFutureApprovedByOwner(userId, now)) {
            nextByItem.putIfAbsent(b.getItem().getId(), toShort(b));
        }

        return items.stream()
                .map(item -> {
                    ItemDto dto = ItemMapper.toItemDto(item);
                    dto.setLastBooking(lastByItem.get(item.getId()));
                    dto.setNextBooking(nextByItem.get(item.getId()));
                    dto.setComments(
                            commentRepository.findAllByItemId(item.getId()).stream()
                                    .map(CommentMapper::toDto)
                                    .toList()
                    );
                    return dto;
                })
                .toList();
    }

    private BookingShortDto toShort(Booking b) {
        BookingShortDto dto = new BookingShortDto();
        dto.setId(b.getId());
        dto.setBookerId(b.getBooker().getId());
        dto.setStart(b.getStart());
        dto.setEnd(b.getEnd());
        return dto;
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        List<Item> items = itemRepository.searchByText(text);
        return items.stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public CommentDto addComment(Long itemId, CommentDto commentDto, Long authorId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NoSuchElementException("Вещь не найдена"));

        User author = userRepository.findById(authorId).orElseThrow(() -> new NoSuchElementException("Пользователь не найден"));

        boolean hasFinishedRental = bookingRepository
                .existsByItemIdAndBookerIdAndEndBefore(itemId, authorId, LocalDateTime.now());

        if (!hasFinishedRental) {
            throw new ValidationException(
                    "Пользователь не может оставить отзыв, нет завершённой аренды вещи"
            );
        }

        Comment comment = CommentMapper.toEntity(commentDto.getText(), item, author);
        Comment saved = commentRepository.save(comment);
        return CommentMapper.toDto(saved);
    }
}
