package ru.practicum.mainService.user.mapper;

import ru.practicum.mainService.user.dto.NewUserDto;
import ru.practicum.mainService.user.dto.UserDto;
import ru.practicum.mainService.user.dto.UserShortDto;
import ru.practicum.mainService.user.model.User;

public class UserMapper {
    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User toUser(NewUserDto newUserDto) {
        return User.builder()
                .email(newUserDto.getEmail())
                .name(newUserDto.getName())
                .build();
    }

    public static UserShortDto toUserShortDto(User user) {
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }
}