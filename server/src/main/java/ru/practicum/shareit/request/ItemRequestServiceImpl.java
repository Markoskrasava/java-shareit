package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemShortDto;
import ru.practicum.shareit.item.ItemShortMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDto postRequest(NewItemRequest newItemRequest, Long userId) {
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь не найден"));

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(newItemRequest.getDescription());
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(LocalDateTime.now());

        ItemRequest saved = itemRequestRepository.save(itemRequest);
        return ItemRequestMapper.toItemRequestDto(saved, List.of());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getRequestsByUser(Long userId) {
        List<ItemRequest> requests = itemRequestRepository
                .findAllByRequestorIdOrderByCreatedDesc(userId);

        if (requests.isEmpty()) {
            return List.of();
        }

        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .toList();

        Map<Long, List<ItemShortDto>> itemsByRequest = itemRepository
                .findAllByItemRequestIdIn(requestIds).stream()
                .collect(Collectors.groupingBy(
                        item -> item.getItemRequest().getId(),
                        Collectors.mapping(ItemShortMapper::toShortDto, Collectors.toList())
                ));

        return requests.stream()
                .map(r -> ItemRequestMapper.toItemRequestDto(
                        r,
                        itemsByRequest.getOrDefault(r.getId(), List.of())))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getRequestsByOther(Long userId) {
        List<ItemRequest> requests = itemRequestRepository
                .findAllByRequestorIdNotOrderByCreatedDesc(userId);

        if (requests.isEmpty()) {
            return List.of();
        }

        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .toList();

        Map<Long, List<ItemShortDto>> itemsByRequest = itemRepository
                .findAllByItemRequestIdIn(requestIds).stream()
                .collect(Collectors.groupingBy(
                        item -> item.getItemRequest().getId(),
                        Collectors.mapping(ItemShortMapper::toShortDto, Collectors.toList())
                ));

        return requests.stream()
                .map(r -> ItemRequestMapper.toItemRequestDto(
                        r,
                        itemsByRequest.getOrDefault(r.getId(), List.of())))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestDto getRequestById(Long requestId) {
        ItemRequest request = itemRequestRepository.findById(requestId).orElseThrow(() -> new NoSuchElementException("Запрос не найден"));

        List<ItemShortDto> items = itemRepository
                .findAllByItemRequestId(requestId).stream()
                .map(ItemShortMapper::toShortDto)
                .toList();

        return ItemRequestMapper.toItemRequestDto(request, items);
    }
}
