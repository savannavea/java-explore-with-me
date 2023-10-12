package ru.practicum.service;

import ru.practicum.HitRequestDto;
import ru.practicum.HitResponseDto;

import java.time.LocalDateTime;
import java.util.List;


public interface StatsService {

    HitRequestDto create(HitRequestDto hitRequestDto);

    List<HitResponseDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
