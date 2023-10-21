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
import ru.practicum.mainService.location.dto.LocationDto;
import ru.practicum.mainService.location.mapper.LocationMapper;
import ru.practicum.mainService.location.model.Location;
import ru.practicum.mainService.user.dto.UserShortDto;
import ru.practicum.mainService.user.mapper.UserMapper;
import ru.practicum.mainService.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    public static EventFullDto toEventFullDto(Event event, CategoryDto categoryDto, UserShortDto initiator,
                                              LocationDto locationDto) {
        return EventFullDto.builder()
                .id(event.getId())
                .category(categoryDto)
                .annotation(event.getAnnotation())
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiator(initiator)
                .location(locationDto)
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .build();
    }

    public static Event toEvent(NewEventDto newEventDto, Category category, Location location,
                                User initiator) {
        return Event.builder()
                .category(category)
                .annotation(newEventDto.getAnnotation())
                .createdOn(LocalDateTime.now())
                .description(newEventDto.getDescription())
                .eventDate(newEventDto.getEventDate())
                .initiator(initiator)
                .location(location)
                .paid(newEventDto.getPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .requestModeration(newEventDto.getRequestModeration())
                .state(EventState.PENDING)
                .title(newEventDto.getTitle())
                .build();
    }

    public static EventFullDto toEventFullDto(Event event) {
        return EventFullDto.builder()
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
    }

    public static List<EventFullDto> toEventShortDtoList(Iterable<Event> events) {
        List<EventFullDto> result = new ArrayList<>();

        for (Event event : events) {
            result.add(toEventFullDto(event));
        }
        return result;
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