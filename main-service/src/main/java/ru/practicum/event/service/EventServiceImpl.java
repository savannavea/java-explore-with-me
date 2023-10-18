package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.category.service.CategoryService;
import ru.practicum.event.dto.*;
import ru.practicum.event.enums.EventSortType;
import ru.practicum.event.enums.EventState;
import ru.practicum.event.enums.StateAction;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.location.dto.LocationDto;
import ru.practicum.location.mapper.LocationMapper;
import ru.practicum.location.model.Location;
import ru.practicum.location.repository.LocationRepository;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.enums.RequestStatus;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static ru.practicum.event.enums.EventState.PUBLISHED;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final UserService userService;
    private final CategoryService categoryService;

    @Override
    public List<EventFullDto> getAllEventsByUserId(Long userId, Integer from, Integer size) {
        userService.getUserOrElseThrow(userId);
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findByInitiatorId(userId, pageRequest);

        return EventMapper.toEventShortDtoList(events);
    }

    @Override
    public EventFullDto createEvents(Long userId, NewEventDto newEventDto) {
        validateEventDate(newEventDto.getEventDate());

        User user = userService.getUserOrElseThrow(userId);
        Location location = getLocationOrAddNew(newEventDto.getLocation());

        Category category = categoryRepository.findById(newEventDto.getCategory()).orElseThrow(() ->
                new NotFoundException(String.format("Category with id=%s was not found", newEventDto.getCategory())));

        Event event = EventMapper.toEvent(newEventDto, category, location, user);

        Event savedEvent = eventRepository.save(event);
        return EventMapper.toEventFullDto(savedEvent,
                CategoryMapper.toCategoryDto(savedEvent.getCategory()),
                UserMapper.toUserShortDto(savedEvent.getInitiator()),
                LocationMapper.toLocationDto(savedEvent.getLocation()));
    }

    @Override
    public EventFullDto getEventsByUserId(Long userId, Long eventId) {
        userService.getUserOrElseThrow(userId);
        getEventOrElseThrow(eventId);

        return EventMapper.toEventFullDto(eventRepository.findByInitiatorIdAndId(userId, eventId));
    }

    @Override
    public EventFullDto updateEventsByUser(Long userId, Long eventId, UpdateEventRequestDto requestDto) {
        User user = userService.getUserOrElseThrow(userId);
        Event event = getEventOrElseThrow(eventId);

        if (!user.getId().equals(event.getInitiator().getId())) {
            throw new ConflictException(String.format("User %s is not the initiator of the event %s.", userId, eventId));
        }
        if (event.getState().equals(PUBLISHED)) {
            throw new ConflictException(String.format("User %s cannot update event %s that has already been published.", userId, eventId));
        }

        Event updateEvent = baseUpdateEvent(event, requestDto);

        return EventMapper.toEventFullDto(updateEvent);
    }

    @Override
    public List<ParticipationRequestDto> getRequestUserEvents(Long userId, Long eventId) {
        User user = userService.getUserOrElseThrow(userId);
        Event event = getEventOrElseThrow(eventId);

        if (!user.getId().equals(event.getInitiator().getId())) {
            throw new ConflictException(String.format("User %s is not the initiator of the event %s.",userId, eventId));
        }

        List<Request> requests = requestRepository.findByEventId(eventId);

        return RequestMapper.toRequestDtoList(requests);
    }

    @Override
    public EventRequestStatusUpdateResult updateStatusRequestByUserIdForEvents(Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        User user = userService.getUserOrElseThrow(userId);
        Event event = getEventOrElseThrow(eventId);

        if (!user.getId().equals(event.getInitiator().getId())) {
            throw new ConflictException("Пользователь не инициатор события!");
        }
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            throw new ConflictException("Не требуется модерация и подтверждения заявок");
        }

        Long confirmedRequests = requestRepository.countAllByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() <= (confirmedRequests)) {
            throw new ConflictException("Нельзя подтвердить заявку, если уже достигнут лимит по заявкам на данное событие!");
        }
        List<Request> requestsToUpdate = requestRepository.findAllByIdIn(eventRequestStatusUpdateRequest.getRequestIds());
        List<Request> confirmed = new ArrayList<>();
        List<Request> rejected = new ArrayList<>();

        for (Request request : requestsToUpdate) {

            if (!request.getStatus().equals(RequestStatus.PENDING)) {
                continue;
            }


            if (!request.getEvent().getId().equals(eventId)) {
                rejected.add(request);
                continue;
            }
            if (eventRequestStatusUpdateRequest.getStatus().equals("CONFIRMED")) {
                if (confirmedRequests < event.getParticipantLimit()) {
                    request.setStatus(RequestStatus.CONFIRMED);
                    confirmedRequests++;
                    confirmed.add(request);
                } else {
                    request.setStatus(RequestStatus.REJECTED);
                    rejected.add(request);
                }

            } else {
                request.setStatus(RequestStatus.REJECTED);
                rejected.add(request);
            }
        }
        eventRepository.save(event);
        requestRepository.saveAll(requestsToUpdate);

        return RequestMapper.toUpdateResultDto(confirmed, rejected);
    }

    @Override
    public List<EventShortDto> getEvents(String text, List<Long> categories, Boolean paid, LocalDateTime start, LocalDateTime end, Boolean onlyAvailable, EventSortType sort, Integer from, Integer size, HttpServletRequest request) {
        return null;
    }

    @Override
    public EventFullDto getEventById(Long id, HttpServletRequest request) {
        return null;
    }

    @Override
    public List<EventFullDto> adminGetEvents(List<Long> users, List<EventState> states, List<Long> categories, String rangeStart, String rangeEnd, Integer from, Integer size) {
        return null;
    }

    @Override
    public EventFullDto adminUpdateEvent(Long eventId, UpdateEventRequestDto requestDto) {
        return null;
    }

    @Override
    public Event getEventOrElseEtrow(Long eventId) {
        return eventRepository
                .findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event's id %d doesn't found!", eventId)));
    }

    private Location getLocationOrAddNew(LocationDto locationDto) {
        Location location = locationRepository.findByLatAndLon(
                locationDto.getLat(),
                locationDto.getLon());

        if (Objects.isNull(location)) {
            location = locationRepository.save(LocationMapper.toLocation(locationDto));
        }
        return location;
    }

    private void validateEventDate(LocalDateTime eventDate) {
        if (LocalDateTime.now().plusHours(2).isAfter(eventDate)) {
            throw new BadRequestException(String.format("Event date=%s cannot be before now + 2 hours date.", eventDate));
        }
    }

    private Event getEventOrElseThrow(Long eventId) {
        return eventRepository
                .findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event id %d doesn't found!", eventId)));
    }

    private Event baseUpdateEvent(Event event, UpdateEventRequestDto requestDto) {

        if (requestDto.getAnnotation() != null && !requestDto.getAnnotation().isBlank()) {
            event.setAnnotation(requestDto.getAnnotation());
        }
        if (requestDto.getCategory() != null) {
            event.setCategory(categoryService.getCategoryOrElseThrow(requestDto.getCategory()));
        }
        if (requestDto.getDescription() != null && !requestDto.getDescription().isBlank()) {
            event.setDescription(requestDto.getDescription());
        }
        if (requestDto.getEventDate() != null) {
            event.setEventDate(requestDto.getEventDate());
        }
        if (requestDto.getLocation() != null) {
            event.setLocation(LocationMapper.toLocation(requestDto.getLocation()));
        }
        if (requestDto.getPaid() != null) {
            event.setPaid(requestDto.getPaid());
        }
        if (requestDto.getParticipantLimit() != null) {
            event.setParticipantLimit(requestDto.getParticipantLimit());
        }
        if (requestDto.getRequestModeration() != null) {
            event.setRequestModeration(requestDto.getRequestModeration());
        }
        if (requestDto.getStateAction() != null) {
            if (requestDto.getStateAction() == StateAction.PUBLISH_EVENT) {
                event.setState(PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else if (requestDto.getStateAction() == StateAction.REJECT_EVENT ||
                    requestDto.getStateAction() == StateAction.CANCEL_REVIEW) {
                event.setState(EventState.CANCELED);
            } else if (requestDto.getStateAction() == StateAction.SEND_TO_REVIEW) {
                event.setState(EventState.PENDING);
            }
        }
        if (requestDto.getTitle() != null && !requestDto.getTitle().isBlank()) {
            event.setTitle(requestDto.getTitle());
        }

        locationRepository.save(event.getLocation());
        return eventRepository.save(event);
    }
}
