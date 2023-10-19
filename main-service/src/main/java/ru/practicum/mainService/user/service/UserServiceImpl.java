package ru.practicum.mainService.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainService.exception.NotFoundException;
import ru.practicum.mainService.user.dto.UserDto;
import ru.practicum.mainService.user.dto.UserShortDto;
import ru.practicum.mainService.user.mapper.UserMapper;
import ru.practicum.mainService.user.model.User;
import ru.practicum.mainService.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);

        return userRepository
                .getAllUsers(ids, pageable)
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto create(UserShortDto userShortDto) {
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(userShortDto)));
    }

    @Override
    public void deleteUserById(Long userId) {
        getUserOrElseThrow(userId);
        userRepository.deleteById(userId);
    }

    @Override
    public User getUserOrElseThrow(Long id) {
        return userRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("User's id %d doesn't found!", id)));
    }
}