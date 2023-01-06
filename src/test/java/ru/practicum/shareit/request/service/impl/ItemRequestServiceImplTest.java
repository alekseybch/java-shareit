package ru.practicum.shareit.request.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
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
import ru.practicum.shareit.user.db.model.User;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.LongSupplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.global.utility.PageableConverter.getPageable;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemRequestMapper itemRequestMapper;
    @Mock
    private ItemMapper itemMapper;
    @Mock
    private UserService userService;
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private final User user1 = new User();
    private final User user2 = new User();
    private final Item item1 = new Item();
    private final ItemRequest itemRequest1 = new ItemRequest();
    private final ItemForRequestResponseDto itemForRequestResponseDto = new ItemForRequestResponseDto();
    private final ItemReqRequestDto itemReqRequestDto = new ItemReqRequestDto();
    private final ItemReqResponseDto itemReqResponseDto = new ItemReqResponseDto();
    private final Long requestorId = 1L;
    private final Long requestorNotFound = -1L;
    private final Long requestId = 1L;
    private final Long requestNotFound = 1L;

    @BeforeEach
    void setUp() {
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, itemRepository,
                itemRequestMapper, itemMapper, userService);

        user1.setId(1L);
        user1.setName("user");
        user1.setEmail("user@user.com");

        user2.setId(2L);
        user2.setName("other");
        user2.setEmail("other@other.com");

        item1.setId(1L);
        item1.setName("drill");
        item1.setDescription("simple drill");
        item1.setAvailable(true);
        item1.setOwner(user1);
        item1.setRequest(itemRequest1);

        itemForRequestResponseDto.setId(item1.getId());
        itemForRequestResponseDto.setName(item1.getName());
        itemForRequestResponseDto.setDescription(item1.getDescription());
        itemForRequestResponseDto.setAvailable(item1.getAvailable());
        itemForRequestResponseDto.setOwnerId(item1.getOwner().getId());
        itemForRequestResponseDto.setRequestId(item1.getRequest().getId());

        itemReqRequestDto.setRequestorId(user2.getId());
        itemReqRequestDto.setDescription("need drill");

        itemRequest1.setId(1L);
        itemRequest1.setDescription("need drill");
        itemRequest1.setRequestor(user2);
        itemRequest1.setCreated(LocalDateTime.now());

        itemReqResponseDto.setId(1L);
        itemReqResponseDto.setDescription("need drill");
        itemReqResponseDto.setCreated(itemRequest1.getCreated());
        itemReqResponseDto.setItems(List.of(itemForRequestResponseDto));
    }

    @Test
    void getItemRequests_whenRequestorFound_thenReturnedItemRequests() {
        LongSupplier longSuplier = () -> Long.parseLong("10");
        Page<ItemRequest> pageItemRequests = PageableExecutionUtils
                .getPage(List.of(itemRequest1), getPageable(0, 10, Sort.Direction.DESC,
                        "created"), longSuplier);

        when(userService.getById(anyLong())).thenReturn(toUserDto(user2));
        when(itemRequestRepository.getItemRequestsByRequestorId(anyLong(), any()))
                .thenReturn(pageItemRequests);
        when(itemRequestMapper.toItemReqDto(any())).thenReturn(itemReqResponseDto);
        when(itemRepository.findItemsByRequestId(anyList())).thenReturn(List.of(item1));
        when(itemMapper.toItemForRequestDto(any())).thenReturn(itemForRequestResponseDto);

        List<ItemReqResponseDto> actualItemRequests = itemRequestService.getItemRequests(requestorId, 0, 10);

        verify(userService, times(1)).getById(anyLong());
        verify(itemRequestRepository, times(1)).getItemRequestsByRequestorId(anyLong(), any());
        verify(itemRequestMapper, times(1)).toItemReqDto(any());
        verify(itemRepository, times(1)).findItemsByRequestId(anyList());
        verify(itemMapper, times(1)).toItemForRequestDto(any());
        assertEquals(actualItemRequests.get(0).getId(), itemRequest1.getId());
    }

    @Test
    void getItemRequests_whenRequestorNotFound_thenEntityNotFoundException() {
        when(userService.getById(anyLong())).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () ->
                itemRequestService.getItemRequests(requestorNotFound, 0, 10));
    }

    @Test
    void getById_whenRequestorAndRequestIdFound_thenReturnedItemRequest() {
        when(userService.getById(anyLong())).thenReturn(toUserDto(user2));
        when(itemRequestRepository.getReferenceById(anyLong())).thenReturn(itemRequest1);
        when(itemRequestMapper.toItemReqDto(any())).thenReturn(itemReqResponseDto);
        when(itemRepository.getItemsByRequestId(any())).thenReturn(List.of(item1));
        when(itemMapper.toItemForRequestDto(any())).thenReturn(itemForRequestResponseDto);

        ItemReqResponseDto actualItemRequest = itemRequestService.getById(requestorId, requestId);

        verify(userService, times(1)).getById(anyLong());
        verify(itemRequestRepository, times(1)).getReferenceById(anyLong());
        verify(itemRequestMapper, times(1)).toItemReqDto(any());
        verify(itemRepository, times(1)).getItemsByRequestId(any());
        verify(itemMapper, times(1)).toItemForRequestDto(any());
        assertEquals(actualItemRequest.getId(), itemRequest1.getId());
    }

    @Test
    void getById_whenRequestorNotFound_thenEntityNotFoundException() {
        when(userService.getById(anyLong())).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () ->
                itemRequestService.getById(requestorNotFound, requestId));
    }

    @Test
    void getById_whenRequestNotFound_thenEntityNotFoundException() {
        when(userService.getById(anyLong())).thenReturn(toUserDto(user2));
        when(itemRequestRepository.getReferenceById(anyLong())).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () ->
                itemRequestService.getById(requestorId, requestNotFound));
    }

    @Test
    void getAllByOtherRequestors_whenInvoked_thenReturnedOtherRequestorsItemRequests() {
        Long notRequestorId = 3L;
        LongSupplier longSuplier = () -> Long.parseLong("10");
        Page<ItemRequest> pageItemRequests = PageableExecutionUtils
                .getPage(List.of(itemRequest1), getPageable(0, 10, Sort.Direction.DESC,
                        "created"), longSuplier);

        when(userService.getById(anyLong())).thenReturn(toUserDto(user1));
        when(itemRequestRepository.getItemRequestsByRequestorId(anyLong(), any()))
                .thenReturn(pageItemRequests);
        when(itemRequestMapper.toItemReqDto(any())).thenReturn(itemReqResponseDto);
        when(itemRepository.findItemsByRequestId(anyList())).thenReturn(List.of(item1));
        when(itemMapper.toItemForRequestDto(any())).thenReturn(itemForRequestResponseDto);

        List<ItemReqResponseDto> actualItemRequests = itemRequestService.getItemRequests(notRequestorId, 0, 10);

        verify(userService, times(1)).getById(anyLong());
        verify(itemRequestRepository, times(1)).getItemRequestsByRequestorId(anyLong(), any());
        verify(itemRequestMapper, times(1)).toItemReqDto(any());
        verify(itemRepository, times(1)).findItemsByRequestId(anyList());
        verify(itemMapper, times(1)).toItemForRequestDto(any());
        assertEquals(actualItemRequests.get(0).getId(), itemRequest1.getId());
    }

    @Test
    void save_whenInvoked_thenSaveItemRequest() {
        when(itemRequestMapper.toItemRequest(any())).thenReturn(itemRequest1);
        when(itemRequestRepository.save(any())).thenReturn(itemRequest1);
        when(itemRequestMapper.toItemReqDto(any())).thenReturn(itemReqResponseDto);

        ItemReqResponseDto actualItemRequest = itemRequestService.save(itemReqRequestDto);

        verify(itemRequestMapper, times(1)).toItemRequest(any());
        verify(itemRequestRepository, times(1)).save(any());
        verify(itemRequestMapper, times(1)).toItemReqDto(any());
        assertEquals(actualItemRequest.getId(), itemRequest1.getId());
    }

    @Test
    void save_whenRequestorNotFound_thenNotFoundException() {
        itemRequest1.setRequestor(null);

        when(itemRequestMapper.toItemRequest(any())).thenReturn(itemRequest1);

        assertThrows(NotFoundException.class, () -> itemRequestService.save(itemReqRequestDto));
    }

    private UserResponseDto toUserDto(User user) {
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(user.getId());
        userResponseDto.setName(user.getName());
        userResponseDto.setEmail(user.getEmail());
        return userResponseDto;
    }
}