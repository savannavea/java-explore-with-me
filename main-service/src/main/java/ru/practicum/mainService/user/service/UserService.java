package ru.practicum.mainService.user.service;

import ru.practicum.mainService.user.dto.NewUserDto;
import ru.practicum.mainService.user.dto.UserDto;
import ru.practicum.mainService.user.model.User;

import java.util.List;

public interface UserService {
    List<UserDto> getUsers(List<Long> ids, Integer from, Integer size);

    UserDto create(NewUserDto newUserDto);

    void deleteUserById(Long userId);

    User getUserOrElseThrow(Long id);
}
