package ru.practicum.mainService.event.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.mainService.event.enums.EventState;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class Criteria {
    private List<Long> users;

    private List<EventState> states;

    private List<Long> categories;

    private LocalDateTime rangeStart;

    private LocalDateTime rangeEnd;

    private Integer from;

    private Integer size;
}
