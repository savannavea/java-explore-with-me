package ru.practicum.mainService.event.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.mainService.event.enums.EventSortType;

import java.time.LocalDateTime;
import java.util.List;

@Data
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