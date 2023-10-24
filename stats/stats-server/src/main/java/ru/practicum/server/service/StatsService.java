package ru.practicum.server.service;

import ru.practicum.statsDto.HitRequestDto;
import ru.practicum.statsDto.HitResponseDto;

import java.time.LocalDateTime;
import java.util.List;


public interface StatsService {

    HitRequestDto create(HitRequestDto hitRequestDto);

    List<HitResponseDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
