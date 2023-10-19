package ru.practicum.mainService.request.service;

import ru.practicum.mainService.request.dto.ParticipationRequestDto;
import ru.practicum.mainService.request.model.Request;

import java.util.List;

public interface RequestService {

    List<ParticipationRequestDto> getRequestById(Long userId);

    ParticipationRequestDto create(Long userId, Long eventId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);

    Request getRequestOrElseThrow(Long id);
}
