package ru.practicum.request.service;

import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.model.Request;

import java.util.List;

public interface RequestService {

    List<ParticipationRequestDto> getRequestById(Long userId);

    ParticipationRequestDto create(Long userId, Long eventId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);

    Request getRequestOrElseThrow(Long id);
}
