package ru.practicum.shareit.user.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Getter
@Setter
public class UserPatchDto {
    @Size(max = 70, message = "must not be more than 70 characters.")
    private String name;
    @Size(max = 254, message = "must not be more than 254 characters.")
    @Email(message = "invalid format.")
    private String email;
}
