package ru.practicum.event.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.event.enums.EventSortType;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class CriteriaPub {
    private String text;

    private List<Long> categories;

    private Boolean paid;

    private LocalDateTime rangeStart;

    private LocalDateTime rangeEnd;

    private Boolean onlyAvailable;

    private EventSortType eventSortType;

    private Integer from;

    private Integer size;
}