package ru.practicum.user.service;

import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserShortDto;

import java.util.List;

public interface UserService {
    UserDto create(UserShortDto userShortDto);

    List<UserDto> getUsers(List<Long> ids, Integer from, Integer size);

    void deleteUserById(Long userId);
}
