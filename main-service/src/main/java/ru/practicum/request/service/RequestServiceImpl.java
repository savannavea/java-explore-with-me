package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.enums.EventState;
import ru.practicum.event.model.Event;
import ru.practicum.event.service.EventService;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.enums.RequestStatus;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final EventService eventService;
    private final UserService userService;

    @Override
    public List<ParticipationRequestDto> getRequestById(Long userId) {
        userService.getUserOrElseThrow(userId);
        return requestRepository
                .findByRequesterId(userId)
                .stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto create(Long userId, Long eventId) {
        User user = userService.getUserOrElseThrow(userId);
        Event event = eventService.getEventOrElseThrow(eventId);
        Request requestExist = requestRepository.findOneByEventIdAndRequesterId(eventId, userId);

        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Cant make request for unpublished request");
        }

        if (!Objects.isNull(requestExist)) {
            throw new ConflictException(String.format("Event with id=%s and requester with id=%s already exist",
                    eventId, userId));
        }

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException(String.format("Event initiator with id=%s cant make request for their event",
                    userId));
        }

        if (event.getParticipantLimit() != 0 &&
                event.getParticipationRequests().size() >= event.getParticipantLimit()) {
            throw new ConflictException("Participant limit for request is exceeded");
        }

        Request request = Request.builder()
                .requester(user)
                .event(event)
                .created(LocalDateTime.now())
                .build();

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
        } else {
            request.setStatus(RequestStatus.PENDING);
        }

        return RequestMapper.toRequestDto(requestRepository.save(request));
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        User user = userService.getUserOrElseThrow(userId);
        Request request = getRequestOrElseThrow(requestId);

        if (!user.getId().equals(request.getRequester().getId())) {
            throw new ConflictException(String.format("User with id=%s can't cancel request with id=%s",
                    userId, requestId));
        }
        request.setStatus(RequestStatus.CANCELED);

        return RequestMapper.toRequestDto(requestRepository.save(request));
    }

    @Override
    public Request getRequestOrElseThrow(Long id) {
        return requestRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Request's id %d doesn't found!", id)));
    }
}