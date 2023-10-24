package ru.practicum.mainService.event.service;

import ru.practicum.mainService.event.dto.*;
import ru.practicum.mainService.event.enums.EventSortType;
import ru.practicum.mainService.event.enums.EventState;
import ru.practicum.mainService.event.model.Event;
import ru.practicum.mainService.request.dto.ParticipationRequestDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    List<EventFullDto> getAllEventsByUserId(Long userId, Integer from, Integer size);

    EventFullDto createEvents(Long userId, NewEventDto newEventDto);

    EventFullDto getEventsByUserId(Long userId, Long eventId);

    EventFullDto updateEventsByUser(Long userId, Long eventId, UpdateEventRequestDto requestDto);

    List<ParticipationRequestDto> getRequestUserEvents(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateStatusRequestByUserIdForEvents(Long userId, Long eventId,
                                                                        EventRequestStatusUpdateRequest requestDto);

    List<EventShortDto> getEvents(String text, List<Long> categories, Boolean paid, LocalDateTime start,
                                  LocalDateTime end, Boolean onlyAvailable, EventSortType sort, Integer from,
                                  Integer size, HttpServletRequest request);

    EventFullDto getEventById(Long id, HttpServletRequest request);

    List<EventFullDto> adminGetEvents(List<Long> users, List<EventState> states, List<Long> categories,
                                      String start, String end, Integer from, Integer size);

    EventFullDto adminUpdateEvent(Long eventId, UpdateEventRequestDto requestDto);

    Event getEventOrElseThrow(Long eventId);
}
