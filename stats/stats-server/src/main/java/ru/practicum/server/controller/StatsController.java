package ru.practicum.server.controller;

import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.server.service.StatsService;
import ru.practicum.statsDto.HitRequestDto;
import ru.practicum.statsDto.HitResponseDto;

import java.time.LocalDateTime;
import java.util.List;

@Validated
@RestController
@RequestMapping
@AllArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<HitRequestDto> create(@RequestBody HitRequestDto hitRequestDto) {
        return new ResponseEntity<>(statsService.create(hitRequestDto), HttpStatus.CREATED);
    }

    @GetMapping("/stats")
    public List<HitResponseDto> getStats(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                         LocalDateTime start,
                                         @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                         LocalDateTime end,
                                         @RequestParam(required = false) List<String> uris,
                                         @RequestParam(defaultValue = "false") Boolean unique) {
        return statsService.getStats(start, end, uris, unique);
    }
}