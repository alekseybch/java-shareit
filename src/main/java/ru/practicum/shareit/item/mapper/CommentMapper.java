package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.global.mapper.EntityMapper;
import ru.practicum.shareit.item.db.model.Comment;
import ru.practicum.shareit.item.db.repository.ItemRepository;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.user.db.repository.UserRepository;

@Mapper(componentModel = "spring", uses = {ItemRepository.class, UserRepository.class})
public interface CommentMapper {
    @Mapping(target = "author", source = "dto.authorId", qualifiedBy = EntityMapper.class)
    @Mapping(target = "item", source = "dto.itemId", qualifiedBy = EntityMapper.class)
    Comment toComment(CommentRequestDto dto);

    @Mapping(target = "authorName", source = "author.name")
    CommentResponseDto toCommentDto(Comment comment);
}
