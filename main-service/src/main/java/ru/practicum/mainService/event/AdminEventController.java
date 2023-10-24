package ru.practicum.mainService.event;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainService.event.dto.EventFullDto;
import ru.practicum.mainService.event.dto.UpdateEventRequestDto;
import ru.practicum.mainService.event.enums.EventState;
import ru.practicum.mainService.event.service.EventService;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
public class AdminEventController {
    private final EventService eventService;

    @GetMapping()
    public List<EventFullDto> adminGetEvents(@RequestParam(required = false) List<Long> users,
                                             @RequestParam(required = false) List<EventState> states,
                                             @RequestParam(required = false) List<Long> categories,
                                             @RequestParam(required = false) String rangeStart,
                                             @RequestParam(required = false) String rangeEnd,
                                             @RequestParam(defaultValue = "0") Integer from,
                                             @RequestParam(defaultValue = "10") Integer size) {

        return eventService.adminGetEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto patchAdminEvent(@PathVariable @Min(1) Long eventId,
                                        @RequestBody @Validated UpdateEventRequestDto requestDto) {
        return eventService.adminUpdateEvent(eventId, requestDto);
    }
}