package ru.practicum.event.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.event.enums.EventSortType;
import ru.practicum.event.enums.EventState;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class Criteria {
    private Boolean published;

    private String text;

    private List<Long> users;

    private List<EventState> states;

    private List<Long> categories;

    private Boolean paid;

    private LocalDateTime start;

    private LocalDateTime end;

    private EventSortType sort;

    private List<Long> locationIds;

    private int from;

    private int size;
}
