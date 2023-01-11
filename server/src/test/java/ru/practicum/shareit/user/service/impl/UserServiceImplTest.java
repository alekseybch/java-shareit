package ru.practicum.shareit.user.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.db.model.User;
import ru.practicum.shareit.user.db.repository.UserRepository;
import ru.practicum.shareit.user.dto.UserPatchDto;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import javax.persistence.EntityNotFoundException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserServiceImpl userService;

    private final User user1 = new User();
    private final UserRequestDto userRequestDto = new UserRequestDto();
    private final UserPatchDto userPatchDto = new UserPatchDto();
    private final User changedUser = new User();
    private final Long userId = 1L;
    private final Long userNotFound = -1L;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, userMapper);

        user1.setId(1L);
        user1.setName("user");
        user1.setEmail("user@user.com");

        userRequestDto.setName("user");
        userRequestDto.setEmail("user@user.com");

        userPatchDto.setName("changed");
        userPatchDto.setEmail("changed@email.com");

        changedUser.setId(1L);
        changedUser.setName(userPatchDto.getName());
        changedUser.setEmail(userPatchDto.getEmail());
    }

    @Test
    void getUsers_whenInvoked_thenReturnedUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user1));
        when(userMapper.toUserDto(any())).thenReturn(toUserDto(user1));

        List<UserResponseDto> actualUsers = userService.getUsers();

        verify(userRepository, times(1)).findAll();
        verify(userMapper, times(1)).toUserDto(any());
        assertEquals(1, actualUsers.size());
    }

    @Test
    void getById_whenUserFound_thenReturnedUser() {
        when(userRepository.getReferenceById(anyLong())).thenReturn(user1);
        when(userMapper.toUserDto(any())).thenReturn(toUserDto(user1));

        UserResponseDto actualUser = userService.getUser(userId);

        verify(userRepository, times(1)).getReferenceById(anyLong());
        verify(userMapper, times(1)).toUserDto(any());
        assertEquals(actualUser.getId(), user1.getId());
    }

    @Test
    void getById_whenUserNotFound_thenNotFoundException() {
        when(userRepository.getReferenceById(anyLong())).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> userService.getUser(userId));
    }

    @Test
    void save_whenInvoked_thenSaveUser() {
        when(userMapper.toUser(any())).thenReturn(user1);
        when(userRepository.save(any())).thenReturn(user1);
        when(userMapper.toUserDto(any())).thenReturn(toUserDto(user1));

        UserResponseDto userResponseDto = userService.createUser(userRequestDto);

        verify(userMapper, times(1)).toUser(any());
        verify(userRepository, times(1)).save(any());
        verify(userMapper, times(1)).toUserDto(any());
        assertEquals(userResponseDto.getId(), user1.getId());
    }

    @Test
    void change_whenUserFound_thenChangedUser() {
        when(userRepository.getReferenceById(anyLong())).thenReturn(user1);
        when(userRepository.save(any())).thenReturn(changedUser);
        when(userMapper.toUserDto(any())).thenReturn(toUserDto(changedUser));

        UserResponseDto actualUser = userService.updateUser(userId, userPatchDto);

        verify(userRepository, times(1)).getReferenceById(anyLong());
        verify(userRepository, times(1)).save(any());
        verify(userMapper, times(1)).toUserDto(any());
        assertEquals(actualUser.getName(), userPatchDto.getName());
        assertEquals(actualUser.getEmail(), userPatchDto.getEmail());
    }

    @Test
    void change_whenUserNotFound_thenNotFoundException() {
        when(userRepository.getReferenceById(anyLong())).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> userService.updateUser(userNotFound, userPatchDto));
    }

    @Test
    void delete_whenInvoke_thenDeleteUser() {
        doNothing().when(userRepository).deleteById(anyLong());

        userService.deleteUser(userId);

        verify(userRepository, times(1)).deleteById(anyLong());
    }

    private UserResponseDto toUserDto(User user) {
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(user.getId());
        userResponseDto.setName(user.getName());
        userResponseDto.setEmail(user.getEmail());
        return userResponseDto;
    }
}