package ru.practicum.shareit.item.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import ru.practicum.shareit.booking.db.model.Booking;
import ru.practicum.shareit.booking.db.model.enums.BookingStatus;
import ru.practicum.shareit.booking.db.repository.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingBookerDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.global.exception.NotFoundException;
import ru.practicum.shareit.global.exception.NotItemBookedException;
import ru.practicum.shareit.global.exception.NotItemOwnerException;
import ru.practicum.shareit.item.db.model.Comment;
import ru.practicum.shareit.item.db.model.Item;
import ru.practicum.shareit.item.db.repository.CommentRepository;
import ru.practicum.shareit.item.db.repository.ItemRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.db.model.User;
import ru.practicum.shareit.user.dto.UserResponseDto;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemMapper itemMapper;
    @Mock
    private BookingMapper bookingMapper;
    @Mock
    private CommentMapper commentMapper;
    @InjectMocks
    ItemServiceImpl itemService;

    private final User user1 = new User();
    private final User user2 = new User();
    private final Item item1 = new Item();
    private final ItemPatchDto patchDto = new ItemPatchDto();
    private final ItemRequestDto itemRequestDto = new ItemRequestDto();
    private final Booking booking1 = new Booking();
    private final Comment comment1 = new Comment();
    private final CommentRequestDto commentRequestDto = new CommentRequestDto();
    private final Long userId = 1L;
    private final Long notOwnerId = 3L;
    private final Long itemId = 1L;
    private final Long itemNotFound = -1L;

    @BeforeEach
    void setUp() {
        itemService = new ItemServiceImpl(itemRepository, commentRepository, bookingRepository, itemMapper,
                bookingMapper, commentMapper);

        user1.setId(1L);
        user1.setName("user");
        user1.setEmail("user@user.com");

        user2.setId(2L);
        user2.setName("other");
        user2.setEmail("other@other.com");

        itemRequestDto.setName("drill");
        itemRequestDto.setDescription("simple drill");
        itemRequestDto.setAvailable(true);
        itemRequestDto.setOwnerId(user1.getId());

        item1.setId(1L);
        item1.setName("drill");
        item1.setDescription("simple drill");
        item1.setAvailable(true);
        item1.setOwner(user1);

        patchDto.setName("changed");
        patchDto.setDescription("new description");
        patchDto.setAvailable(false);

        booking1.setId(1L);
        booking1.setItem(item1);
        booking1.setBooker(user2);
        booking1.setStart(LocalDateTime.now().minusDays(1));
        booking1.setEnd(booking1.getStart().plusDays(1));
        booking1.setStatus(BookingStatus.WAITING);

        comment1.setId(1L);
        comment1.setItem(item1);
        comment1.setAuthor(user2);
        comment1.setText("nice drill");
        comment1.setCreated(booking1.getStart().plusDays(2));

        commentRequestDto.setItemId(item1.getId());
        commentRequestDto.setAuthorId(user2.getId());
        commentRequestDto.setText("nice drill");
    }

    @Test
    void getItems_whenUserFound_thenReturnedItems() {
        when(itemRepository.getItemsByOwnerId(anyLong(), any())).thenReturn(List.of(item1));
        when(itemMapper.toItemDto(any())).thenReturn(toItemDto(item1));
        when(bookingRepository.findLastAndNextByIdList(anyList(), any())).thenReturn(List.of(booking1));
        when(bookingMapper.toBookingBookerDto(any())).thenReturn(toBookingBookerDto(booking1));
        when(commentRepository.findCommentsByItemIds(anyList())).thenReturn(List.of(comment1));
        when(commentMapper.toCommentDto(any())).thenReturn(toCommentDto(comment1));

        List<ItemResponseDto> actualItems = itemService.getItems(userId, 0, 10);

        verify(itemRepository, times(1)).getItemsByOwnerId(anyLong(), any());
        verify(itemMapper, times(1)).toItemDto(any());
        verify(bookingRepository, times(1)).findLastAndNextByIdList(anyList(), any());
        verify(bookingMapper, times(1)).toBookingBookerDto(any());
        verify(commentRepository, times(1)).findCommentsByItemIds(anyList());
        verify(commentMapper, times(1)).toCommentDto(any());
        assertEquals(1, actualItems.size());
        assertEquals(actualItems.get(0).getId(), item1.getId());
    }

    @Test
    void getItem_whenUserFound_thenReturnedItem() {
        when(itemRepository.getReferenceById(anyLong())).thenReturn(item1);
        when(itemMapper.toItemDto(any())).thenReturn(toItemDto(item1));
        when(bookingRepository.findLastAndNextById(anyLong(), any())).thenReturn(List.of(booking1));
        when(bookingMapper.toBookingBookerDto(any())).thenReturn(toBookingBookerDto(booking1));
        when(commentRepository.findCommentsByItemId(any())).thenReturn(List.of(comment1));
        when(commentMapper.toCommentDto(any())).thenReturn(toCommentDto(comment1));

        ItemResponseDto actualItem = itemService.getItem(itemId, userId);

        verify(itemRepository, times(1)).getReferenceById(anyLong());
        verify(itemMapper, times(1)).toItemDto(any());
        verify(bookingRepository, times(1)).findLastAndNextById(anyLong(), any());
        verify(bookingMapper, times(1)).toBookingBookerDto(any());
        verify(commentRepository, times(1)).findCommentsByItemId(anyLong());
        verify(commentMapper, times(1)).toCommentDto(any());
        assertEquals(actualItem.getId(), item1.getId());
    }

    @Test
    void getItem_whenUserNotOwner_thenReturnedItemWithoutLastAndNextBookings() {
        when(itemRepository.getReferenceById(anyLong())).thenReturn(item1);
        when(itemMapper.toItemDto(any())).thenReturn(toItemDto(item1));
        when(commentRepository.findCommentsByItemId(any())).thenReturn(List.of(comment1));
        when(commentMapper.toCommentDto(any())).thenReturn(toCommentDto(comment1));

        ItemResponseDto actualItem = itemService.getItem(itemId, notOwnerId);

        verify(itemRepository, times(1)).getReferenceById(anyLong());
        verify(itemMapper, times(1)).toItemDto(any());
        verify(bookingRepository, times(0)).findLastAndNextById(anyLong(), any());
        verify(bookingMapper, times(0)).toBookingBookerDto(any());
        verify(commentRepository, times(1)).findCommentsByItemId(anyLong());
        verify(commentMapper, times(1)).toCommentDto(any());
        assertEquals(actualItem.getId(), item1.getId());
    }

    @Test
    void getItem_whenUserNotFound_thenEntityNotFoundException() {
        when(itemRepository.getReferenceById(anyLong())).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> itemService.getItem(itemNotFound, userId));
    }

    @Test
    void findItem_whenTextNotEmpty_thenReturnedItems() {
        when(itemRepository.getByText(any(), any())).thenReturn(Page.empty());
        when(bookingRepository.findLastAndNextByIdList(anyList(), any())).thenReturn(Collections.emptyList());
        when(commentRepository.findCommentsByItemIds(anyList())).thenReturn(Collections.emptyList());

        itemService.findItem("text", 0, 10);

        verify(itemRepository, times(1)).getByText(any(), any());
        verify(bookingRepository, times(1)).findLastAndNextByIdList(anyList(), any());
        verify(commentRepository, times(1)).findCommentsByItemIds(anyList());
    }

    @Test
    void findItem_whenTextEmpty_thenReturnedEmptyList() {
        itemService.findItem("", 0, 10);

        verify(itemRepository, times(0)).getByText(any(), any());
    }

    @Test
    void createItem_whenInvoked_thenSaveItem() {
        when(itemMapper.toItem(any())).thenReturn(item1);
        when(itemRepository.save(any())).thenReturn(item1);
        when(itemMapper.toItemDto(any())).thenReturn(toItemDto(item1));

        ItemResponseDto savedItem = itemService.createItem(itemRequestDto);

        verify(itemMapper, times(1)).toItem(any());
        verify(itemRepository, times(1)).save(any());
        verify(itemMapper, times(1)).toItemDto(any());
        assertEquals(savedItem.getId(), item1.getId());
    }

    @Test
    void createItem_whenUserNotFound_thenNotFoundException() {
        item1.setOwner(null);

        when(itemMapper.toItem(any())).thenReturn(item1);

        assertThrows(NotFoundException.class, () -> itemService.createItem(itemRequestDto));
    }

    @Test
    void updateItem_whenInvoked_thenChangeItem() {
        Item itemChanged = item1;
        itemChanged.setName(patchDto.getName());

        when(itemRepository.getReferenceById(anyLong())).thenReturn(item1);
        when(itemRepository.save(any())).thenReturn(item1);
        when(itemMapper.toItemDto(any())).thenReturn(toItemDto(itemChanged));

        ItemResponseDto changedItem = itemService.updateItem(userId, itemId, patchDto);

        verify(itemRepository,times(1)).getReferenceById(anyLong());
        verify(itemRepository,times(1)).save(any());
        verify(itemMapper,times(1)).toItemDto(any());
        assertEquals(changedItem.getName(), "changed");
    }

    @Test
    void updateItem_whenItemNotFound_thenEntityNotFoundException() {
        when(itemRepository.getReferenceById(anyLong())).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> itemService.updateItem(itemNotFound, userId, patchDto));
    }

    @Test
    void updateItem_whenUserNotItemOwner_thenNotItemOwnerException() {
        when(itemRepository.getReferenceById(anyLong())).thenReturn(item1);

        assertThrows(NotItemOwnerException.class, () -> itemService.updateItem(notOwnerId, itemId, patchDto));
    }

    @Test
    void createComment_whenInvoked_thenSaveComment() {
        when(commentMapper.toComment(any())).thenReturn(comment1);
        when(bookingRepository.findByItemIdAndBookerIdAndStatusApproved(anyLong(), anyLong(), any()))
                .thenReturn(booking1);
        when(commentRepository.save(any())).thenReturn(comment1);
        when(commentMapper.toCommentDto(any())).thenReturn(toCommentDto(comment1));

        CommentResponseDto savedComment = itemService.createComment(commentRequestDto);

        verify(commentMapper, times(1)).toComment(any());
        verify(bookingRepository,times(1))
                .findByItemIdAndBookerIdAndStatusApproved(anyLong(), anyLong(), any());
        verify(commentRepository, times(1)).save(any());
        verify(commentMapper, times(1)).toCommentDto(any());
        assertEquals(savedComment.getId(), comment1.getId());
    }

    @Test
    void createComment_whenUserNotFound_thenNotFoundException() {
        comment1.setAuthor(null);

        when(commentMapper.toComment(any())).thenReturn(comment1);

        assertThrows(NotFoundException.class, () -> itemService.createComment(commentRequestDto));
    }

    @Test
    void createComment_whenItemNotFound_thenNotFoundException() {
        comment1.setItem(null);

        when(commentMapper.toComment(any())).thenReturn(comment1);

        assertThrows(NotFoundException.class, () -> itemService.createComment(commentRequestDto));
    }

    @Test
    void createComment_whenItemNotBooked_thenNotFoundException() {
        when(commentMapper.toComment(any())).thenReturn(comment1);
        when(bookingRepository.findByItemIdAndBookerIdAndStatusApproved(anyLong(), anyLong(), any()))
                .thenReturn(null);

        assertThrows(NotItemBookedException.class, () -> itemService.createComment(commentRequestDto));
    }

    private UserResponseDto toUserDto(User user) {
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(user.getId());
        userResponseDto.setName(user.getName());
        userResponseDto.setEmail(user.getEmail());
        return userResponseDto;
    }

    private ItemResponseDto toItemDto(Item item) {
        if (item == null) {
            return null;
        }
        ItemResponseDto itemResponseDto = new ItemResponseDto();
        itemResponseDto.setId(item.getId());
        itemResponseDto.setName(item.getName());
        itemResponseDto.setDescription(item.getDescription());
        itemResponseDto.setAvailable(item.getAvailable());
        itemResponseDto.setOwner(toUserDto(item.getOwner()));
        return itemResponseDto;
    }

    private BookingBookerDto toBookingBookerDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        BookingBookerDto bookingBookerDto = new BookingBookerDto();
        bookingBookerDto.setId(booking.getId());
        bookingBookerDto.setStart(booking.getStart());
        bookingBookerDto.setEnd(booking.getEnd());
        return bookingBookerDto;
    }

    private CommentResponseDto toCommentDto(Comment comment) {
        if (comment == null) {
            return null;
        }
        CommentResponseDto commentResponseDto = new CommentResponseDto();
        commentResponseDto.setId(comment.getId());
        commentResponseDto.setAuthorName(comment.getAuthor().getName());
        commentResponseDto.setText(comment.getText());
        commentResponseDto.setCreated(comment.getCreated());
        return commentResponseDto;
    }
}