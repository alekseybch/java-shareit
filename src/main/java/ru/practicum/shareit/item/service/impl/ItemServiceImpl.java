package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.db.model.Booking;
import ru.practicum.shareit.booking.db.repository.BookingRepository;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.global.exception.NotFoundException;
import ru.practicum.shareit.global.exception.NotItemBookedException;
import ru.practicum.shareit.global.exception.NotItemOwnerException;
import ru.practicum.shareit.global.utility.PageableConverter;
import ru.practicum.shareit.item.db.model.Comment;
import ru.practicum.shareit.item.db.model.Item;
import ru.practicum.shareit.item.db.repository.CommentRepository;
import ru.practicum.shareit.item.db.repository.ItemRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.shareit.global.utility.PageableConverter.getPageable;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ItemResponseDto> getItems(Long userId, Integer from, Integer size) {
        log.info("request to get all user items with id = {}.", userId);
        List<ItemResponseDto> itemsDto = itemRepository.getItemsByOwnerId(userId,
                        getPageable(from, size, Sort.Direction.ASC, "id")).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
        List<Long> itemIds = itemsDto.stream()
                .map(ItemResponseDto::getId)
                .collect(Collectors.toList());
        getBookingsList(itemsDto, itemIds);
        getCommentsList(itemsDto, itemIds);
        return itemsDto;
    }

    @Override
    @Transactional(readOnly = true)
    public ItemResponseDto getById(Long itemId, Long userId) {
        log.info("request to get a item with id = {}.", itemId);
        ItemResponseDto itemDto = itemMapper.toItemDto(itemRepository.getReferenceById(itemId));
        if (itemDto.getOwner().getId().equals(userId)) {
            getBookings(itemDto);
        }
        getComments(itemDto);
        return itemDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemResponseDto> getByText(String text, Integer from, Integer size) {
        log.info("items search request by text = {}.", text);
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        List<ItemResponseDto> itemsDto = itemRepository.getByText(text.toLowerCase(),
                        PageableConverter.getPageable(from, size, Sort.Direction.ASC, "id")).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
        List<Long> itemIds = itemsDto.stream()
                .map(ItemResponseDto::getId)
                .collect(Collectors.toList());
        getBookingsList(itemsDto, itemIds);
        getCommentsList(itemsDto, itemIds);
        return itemsDto;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ItemResponseDto save(ItemRequestDto itemDto) {
        log.info("request to save a item {}.", itemDto);
        Item item = itemMapper.toItem(itemDto);
        if (item.getOwner() == null) {
            throw new NotFoundException(String.format("user with id = %d not found.", itemDto.getOwnerId()));
        }
        Item savedItem = itemRepository.save(item);
        log.info("item with id = {} is saved {}.", savedItem.getId(), savedItem);
        return itemMapper.toItemDto(savedItem);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ItemResponseDto change(Long userId, Long itemId, ItemPatchDto itemDto) {
        log.info("request to change a item with id = {} to {}.", itemId, itemDto);
        Item dbItem = itemRepository.getReferenceById(itemId);
        if (!dbItem.getOwner().getId().equals(userId)) {
            throw new NotItemOwnerException(String.format("user with id = %d does not own item with id = %d.", userId, itemId));
        }
        if (itemDto.getName() != null) {
            dbItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            dbItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            dbItem.setAvailable(itemDto.getAvailable());
        }
        ItemResponseDto changedItem = itemMapper.toItemDto(itemRepository.save(dbItem));
        getBookings(changedItem);
        getComments(changedItem);
        log.info("item with id = {} is changed {}.", changedItem.getId(), changedItem);
        return changedItem;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public CommentResponseDto saveComment(CommentRequestDto commentDto) {
        log.info("request to save a comment from user with id = {} to item with id = {}.",
                commentDto.getAuthorId(), commentDto.getItemId());
        Comment comment = commentMapper.toComment(commentDto);
        if (comment.getAuthor() == null) {
            throw new NotFoundException(String.format("user with id = %d not found.", commentDto.getAuthorId()));
        }
        if (comment.getItem() == null) {
            throw new NotFoundException(String.format("item with id = %d not found.", commentDto.getItemId()));
        }
        LocalDateTime time = LocalDateTime.now();
        if ((bookingRepository.findByItemIdAndBookerIdAndStatusApproved(commentDto.getItemId(),
                commentDto.getAuthorId(), time)) == null) {
            throw new NotItemBookedException(String.format("user with id = %d does booked item with id = %d",
                    commentDto.getAuthorId(), commentDto.getItemId()));
        }
        comment.setCreated(time);
        Comment savedComment = commentRepository.save(comment);
        log.info("user with id = {} is saved comment to item with id = {}.",
                savedComment.getAuthor().getId(), savedComment.getItem().getId());
        return commentMapper.toCommentDto(savedComment);
    }

    private void getBookings(ItemResponseDto itemDto) {
        LocalDateTime time = LocalDateTime.now();
        List<Booking> bookings = bookingRepository.findLastAndNextById(itemDto.getId(), time);
        bookings.forEach(booking -> {
                    if (booking.getStart().isAfter(time)) {
                        itemDto.setNextBooking(bookingMapper.toBookingBookerDto(booking));
                    }
                    if (booking.getEnd().isBefore(time)) {
                        itemDto.setLastBooking(bookingMapper.toBookingBookerDto(booking));
                    }
                });
    }

    private void getBookingsList(List<ItemResponseDto> itemDtoList, List<Long> itemIds) {
        LocalDateTime time = LocalDateTime.now();
        List<Booking> bookings = bookingRepository.findLastAndNextByIdList(itemIds, time);
        itemDtoList.forEach(itemDto -> bookings.forEach(booking -> {
            if (booking.getItem().getId().equals(itemDto.getId()) && booking.getStart().isAfter(time)) {
                itemDto.setNextBooking(bookingMapper.toBookingBookerDto(booking));
            }
            if (booking.getItem().getId().equals(itemDto.getId()) && booking.getEnd().isBefore(time)) {
                itemDto.setLastBooking(bookingMapper.toBookingBookerDto(booking));
            }
        }));
    }

    private void getComments(ItemResponseDto itemDto) {
        List<CommentResponseDto> comments = commentRepository.findCommentsByItemId(itemDto.getId()).stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());
        itemDto.setComments(comments);
    }

    private void getCommentsList(List<ItemResponseDto> itemDtoList, List<Long> itemIds) {
        List<Comment> comments = commentRepository.findCommentsByItemIds(itemIds);
        itemDtoList.forEach(itemDto -> {
                    List<CommentResponseDto> commentsDto = new ArrayList<>();
                    comments.forEach(comment -> {
                        if (comment.getItem().getId().equals(itemDto.getId())) {
                            commentsDto.add(commentMapper.toCommentDto(comment));
                        }
                    });
                    itemDto.setComments(commentsDto);
                });
    }
}
