package ru.practicum.shareit.request;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto postRequest(NewItemRequest newItemRequest, Long userId);

    List<ItemRequestDto> getRequestsByUser(Long userId);

    List<ItemRequestDto> getRequestsByOther(Long userId);

    ItemRequestDto getRequestById(Long requestId);
}
