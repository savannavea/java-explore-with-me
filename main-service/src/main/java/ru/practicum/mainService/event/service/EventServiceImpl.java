package ru.practicum.mainService.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainService.StatisticClient;
import ru.practicum.mainService.category.mapper.CategoryMapper;
import ru.practicum.mainService.category.model.Category;
import ru.practicum.mainService.category.repository.CategoryRepository;
import ru.practicum.mainService.category.service.CategoryService;
import ru.practicum.mainService.event.dto.*;
import ru.practicum.mainService.event.enums.EventSortType;
import ru.practicum.mainService.event.enums.EventState;
import ru.practicum.mainService.event.enums.StateAction;
import ru.practicum.mainService.event.mapper.EventMapper;
import ru.practicum.mainService.event.model.Event;
import ru.practicum.mainService.event.repository.CustomEventRepository;
import ru.practicum.mainService.event.repository.EventRepository;
import ru.practicum.mainService.exception.BadRequestException;
import ru.practicum.mainService.exception.ConflictException;
import ru.practicum.mainService.exception.NotFoundException;
import ru.practicum.mainService.location.dto.LocationDto;
import ru.practicum.mainService.location.mapper.LocationMapper;
import ru.practicum.mainService.location.model.Location;
import ru.practicum.mainService.location.repository.LocationRepository;
import ru.practicum.mainService.request.dto.ParticipationRequestDto;
import ru.practicum.mainService.request.enums.RequestStatus;
import ru.practicum.mainService.request.mapper.RequestMapper;
import ru.practicum.mainService.request.model.Request;
import ru.practicum.mainService.request.repository.RequestRepository;
import ru.practicum.mainService.user.mapper.UserMapper;
import ru.practicum.mainService.user.model.User;
import ru.practicum.mainService.user.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.mainService.event.enums.EventState.PUBLISHED;

@RequiredArgsConstructor
@Service
@Transactional
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final CustomEventRepository customEventRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final StatisticClient statisticClient;

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
            throw new ConflictException(String.format("User %s is not the initiator of the event %s.", userId, eventId));
        }

        List<Request> requests = requestRepository.findByEventId(eventId);

        return RequestMapper.toRequestDtoList(requests);
    }

    @Override
    public EventRequestStatusUpdateResult updateStatusRequestByUserIdForEvents(
            Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        User user = userService.getUserOrElseThrow(userId);
        Event event = getEventOrElseThrow(eventId);

        if (!user.getId().equals(event.getInitiator().getId())) {
            throw new ConflictException("The user is not the initiator of events");
        }
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            throw new ConflictException("No moderation or confirmation of applications required");
        }

        Long confirmedRequests = requestRepository.countAllByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() <= (confirmedRequests)) {
            throw new ConflictException(
                    "You cannot confirm an application if the limit on applications for this event has already been reached");
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
    public List<EventShortDto> getEvents(String text, List<Long> categories, Boolean paid, LocalDateTime start,
                                         LocalDateTime end, Boolean onlyAvailable, EventSortType sort, Integer from,
                                         Integer size, HttpServletRequest request) {
        if (start != null && end != null) {
            if (start.isAfter(end)) {
                throw new ValidationException(String.format("Start date %s later than end date %s.", start, end));
            }
        }

        CriteriaPub criteriaBup = CriteriaPub.builder()
                .text(text)
                .categories(categories)
                .paid(paid)
                .onlyAvailable(onlyAvailable)
                .eventSortType(sort)
                .from(from)
                .size(size)
                .build();

        String ip = request.getRemoteAddr();
        String uri = request.getRequestURI();

        List<Event> events = customEventRepository.getEventsPublic(criteriaBup);

        List<EventShortDto> result = events.stream().map(EventMapper::toToShortDto).collect(Collectors.toList());

        if (result.size() > 0) {
            statisticClient.setViewsNumber(result);

            for (EventShortDto event : result) {
                event.setConfirmedRequests(requestRepository.countAllByEventIdAndStatus(event.getId(),
                        RequestStatus.CONFIRMED));
            }
        }

        statisticClient.saveHit(uri, ip);

        if (result.size() > 0) {
            for (EventShortDto event : result) {
                statisticClient.saveHit("/events/" + event.getId(), ip);
            }
        } else {
            return new ArrayList<>();
        }
        if (criteriaBup.getEventSortType() == EventSortType.VIEWS) {
            return result
                    .stream()
                    .sorted(Comparator.comparingLong(EventShortDto::getViews))
                    .collect(Collectors.toList());
        }

        return result
                .stream()
                .sorted(Comparator.comparing(EventShortDto::getEventDate))
                .collect(Collectors.toList());

    }

    @Override
    public EventFullDto getEventById(Long id, HttpServletRequest request) {
        Event event = eventRepository.findByIdAndAndState(id, EventState.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Published event not found"));
        String ip = request.getRemoteAddr();
        EventFullDto eventFullDto = EventMapper.toEventFullDto(event);
        statisticClient.saveHit("/events/" + id, ip);
        statisticClient.setViewsNumber(eventFullDto);
        eventFullDto.setConfirmedRequests(requestRepository.countAllByEventIdAndStatus(event.getId(),
                RequestStatus.CONFIRMED));

        return eventFullDto;
    }

    @Override
    public List<EventFullDto> adminGetEvents(List<Long> users, List<EventState> states, List<Long> categories,
                                             String start, String end, Integer from, Integer size) {
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        if (start != null) {
            startTime = LocalDateTime.parse(start, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        if (end != null) {
            endTime = LocalDateTime.parse(end, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }

        if (startTime != null && endTime != null) {
            if (startTime.isAfter(endTime)) {
                throw new ValidationException("The start date cannot be after the end date.");
            }
        }
        Criteria criteria = Criteria.builder()
                .users(users)
                .states(states)
                .categories(categories)
                .from(from)
                .size(size)
                .start(startTime)
                .end(endTime)
                .build();
        List<Event> events = customEventRepository.getEvents(criteria);
        return events
                .stream()
                .map(EventMapper::toEventFullDto)
                .map(statisticClient::setViewsNumber)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto adminUpdateEvent(Long eventId, UpdateEventRequestDto requestDto) {
        Event event = getEventOrElseThrow(eventId);

        if (requestDto.getEventDate() != null && event.getPublishedOn() != null &&
                requestDto.getEventDate().isBefore(event.getPublishedOn().plusHours(1))) {
            throw new ValidationException(
                    "The start date of the modified event must be no earlier than an hour from the publication date");
        }
        if (requestDto.getStateAction() != null) {


            switch (requestDto.getStateAction()) {
                case PUBLISH_EVENT:
                    if (event.getState() != EventState.PENDING) {
                        throw new ConflictException("Event state must be PENDING");
                    }
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    break;
                case REJECT_EVENT:
                    if (event.getState() == EventState.PUBLISHED) {
                        throw new ConflictException("Unable to cancel a published event");
                    }
                    event.setState(EventState.CANCELED);
                    break;
                case SEND_TO_REVIEW:
                case CANCEL_REVIEW:
                    if (event.getState() == EventState.PUBLISHED) {
                        throw new ConflictException("Event status must be pending or cancelled");
                    }
                    break;
            }
        }
        baseUpdateEvent(event, requestDto);
        Event toUpdate = eventRepository.save(event);
        EventFullDto eventFullDto = EventMapper.toEventFullDto(toUpdate);
        eventFullDto.setConfirmedRequests(requestRepository.countAllByEventIdAndStatus(event.getId(),
                RequestStatus.CONFIRMED));
        statisticClient.setViewsNumber(eventFullDto);
        return eventFullDto;
    }

    @Override
    public Event getEventOrElseThrow(Long eventId) {
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

    private void validateEventDate(LocalDateTime eventDate) {
        if (LocalDateTime.now().plusHours(2).isAfter(eventDate)) {
            throw new BadRequestException(String.format("Event date=%s cannot be before now + 2 hours date.", eventDate));
        }
    }
}
