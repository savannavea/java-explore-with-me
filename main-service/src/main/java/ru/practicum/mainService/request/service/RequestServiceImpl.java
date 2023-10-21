package ru.practicum.mainService.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainService.event.enums.EventState;
import ru.practicum.mainService.event.model.Event;
import ru.practicum.mainService.event.service.EventService;
import ru.practicum.mainService.exception.ConflictException;
import ru.practicum.mainService.exception.NotFoundException;
import ru.practicum.mainService.request.dto.ParticipationRequestDto;
import ru.practicum.mainService.request.enums.RequestStatus;
import ru.practicum.mainService.request.mapper.RequestMapper;
import ru.practicum.mainService.request.model.Request;
import ru.practicum.mainService.request.repository.RequestRepository;
import ru.practicum.mainService.user.model.User;
import ru.practicum.mainService.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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