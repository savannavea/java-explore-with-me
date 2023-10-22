package ru.practicum.mainService.event.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.mainService.category.dto.CategoryDto;
import ru.practicum.mainService.category.mapper.CategoryMapper;
import ru.practicum.mainService.category.model.Category;
import ru.practicum.mainService.event.dto.EventFullDto;
import ru.practicum.mainService.event.dto.EventShortDto;
import ru.practicum.mainService.event.dto.NewEventDto;
import ru.practicum.mainService.event.enums.EventState;
import ru.practicum.mainService.event.model.Event;
import ru.practicum.mainService.location.mapper.LocationMapper;
import ru.practicum.mainService.location.model.Location;
import ru.practicum.mainService.request.enums.RequestStatus;
import ru.practicum.mainService.user.dto.UserShortDto;
import ru.practicum.mainService.user.mapper.UserMapper;
import ru.practicum.mainService.user.model.User;

@UtilityClass
public class EventMapper {
    public static EventShortDto toEventShortDto(Event event, CategoryDto categoryDto, UserShortDto initiator) {
        return EventShortDto.builder()
                .id(event.getId())
                .category(categoryDto)
                .annotation(event.getAnnotation())
                .confirmedRequests(0L)
                .eventDate(event.getEventDate())
                .initiator(initiator)
                .paid(event.getPaid())
                .title(event.getTitle())
                .build();
    }

    public static Event toEvent(NewEventDto newEventDto, Category category, Location location, User user) {
        return Event.builder()
                .annotation(newEventDto.getAnnotation())
                .category(category)
                .description(newEventDto.getDescription())
                .eventDate(newEventDto.getEventDate())
                .location(location)
                .paid(newEventDto.getPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .requestModeration(newEventDto.getRequestModeration())
                .title(newEventDto.getTitle())
                .initiator(user)
                .state(EventState.PENDING)
                .build();
    }

    public static EventFullDto toEventFullDto(Event event) {
        EventFullDto eventFullDto = EventFullDto.builder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .id(event.getId())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .location(LocationMapper.toLocationDto(event.getLocation()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views(event.getViews())
                .build();

        if (event.getParticipationRequests() != null && !event.getParticipationRequests().isEmpty()) {
            eventFullDto.setConfirmedRequests(event.getParticipationRequests().stream()
                    .filter(participationRequest -> participationRequest.getStatus() == RequestStatus.CONFIRMED)
                    .count());
        } else {
            eventFullDto.setConfirmedRequests(0L);
        }

        return eventFullDto;
    }

    public static EventShortDto toToShortDto(Event event) {
        return EventShortDto.builder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .eventDate(event.getEventDate())
                .id(event.getId())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .build();
    }
}