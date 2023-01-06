package ru.practicum.shareit.request.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.global.exception.NotFoundException;
import ru.practicum.shareit.item.db.model.Item;
import ru.practicum.shareit.item.db.repository.ItemRepository;
import ru.practicum.shareit.item.dto.ItemForRequestResponseDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.db.model.ItemRequest;
import ru.practicum.shareit.request.db.repository.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemReqRequestDto;
import ru.practicum.shareit.request.dto.ItemReqResponseDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.global.utility.PageableConverter.getPageable;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemMapper itemMapper;
    private final UserService userService;

    @Override
    @Transactional(readOnly = true)
    public List<ItemReqResponseDto> getItemRequests(Long userId, Integer from, Integer size) {
        log.info("request to get all user item requests with id = {}.", userId);
        userService.getById(userId);
        List<ItemReqResponseDto> itemReqDtoList = itemRequestRepository.getItemRequestsByRequestorId(userId,
                        getPageable(from, size, Sort.Direction.DESC, "created")).stream()
                .map(itemRequestMapper::toItemReqDto)
                .collect(Collectors.toList());
        getItems(itemReqDtoList);
        return itemReqDtoList;
    }

    @Override
    @Transactional(readOnly = true)
    public ItemReqResponseDto getById(Long requestId, Long userId) {
        log.info("request to get a item request with id = {}.", requestId);
        userService.getById(userId);
        ItemReqResponseDto itemReqDto = itemRequestMapper.toItemReqDto(itemRequestRepository.getReferenceById(requestId));
        getItem(itemReqDto);
        return itemReqDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemReqResponseDto> getAllByOtherRequestors(Long userId, Integer from, Integer size) {
        log.info("request to get all item requests with other users.");
        List<ItemReqResponseDto> itemReqDtoList = itemRequestRepository.findAllByOtherUsers(userId,
                        getPageable(from, size, Sort.Direction.DESC, "created")).stream()
                .map(itemRequestMapper::toItemReqDto)
                .collect(Collectors.toList());
        getItems(itemReqDtoList);
        return itemReqDtoList;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ItemReqResponseDto save(ItemReqRequestDto itemReqDto) {
        log.info("request to save a item request {}.", itemReqDto);
        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemReqDto);
        if (itemRequest.getRequestor() == null) {
            throw new NotFoundException(String.format("user with id = %d not found.", itemReqDto.getRequestorId()));
        }
        itemRequest.setCreated(LocalDateTime.now());
        ItemRequest savedItemRequest = itemRequestRepository.save(itemRequest);
        log.info("item request with id = {} is saved {}.", savedItemRequest.getId(), savedItemRequest);
        return itemRequestMapper.toItemReqDto(savedItemRequest);
    }

    private void getItem(ItemReqResponseDto itemReqDto) {
        List<ItemForRequestResponseDto> items = itemRepository.getItemsByRequestId(itemReqDto.getId()).stream()
                .map(itemMapper::toItemForRequestDto)
                .collect(Collectors.toList());
        itemReqDto.setItems(items);
    }

    private void getItems(List<ItemReqResponseDto> itemReqDtoList) {
        List<Long> itemReqIds = itemReqDtoList.stream()
                .map(ItemReqResponseDto::getId)
                .collect(Collectors.toList());
        List<Item> items = itemRepository.findItemsByRequestId(itemReqIds);
        itemReqDtoList.forEach(itemReqDto -> {
            List<ItemForRequestResponseDto> itemsDto = new ArrayList<>();
            items.forEach(item -> {
                if (item.getRequest().getId().equals(itemReqDto.getId())) {
                    itemsDto.add(itemMapper.toItemForRequestDto(item));
                }
            });
            itemReqDto.setItems(itemsDto);
        });
    }
}
