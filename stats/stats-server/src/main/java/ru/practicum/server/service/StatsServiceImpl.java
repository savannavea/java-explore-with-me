package ru.practicum.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.mapper.StatsMapper;
import ru.practicum.server.repository.StatsRepository;
import ru.practicum.statsDto.HitRequestDto;
import ru.practicum.statsDto.HitResponseDto;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;

    @Override
    public HitRequestDto create(HitRequestDto hitRequestDto) {
        return StatsMapper.toHitRequestDto(statsRepository.save(StatsMapper.toStat(hitRequestDto)));
    }

    @Override
    public List<HitResponseDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("The start time cannot be later than the end date of the range!");
        }
        if (uris == null || uris.isEmpty()) {
            if (unique) {
                return statsRepository.getStatsUnique(start, end);
            } else {
                return statsRepository.getStats(start, end);
            }
        } else {
            if (unique) {
                return statsRepository.getStatsUriUnique(start, end, uris);
            } else {
                return statsRepository.getStatsUri(start, end, uris);
            }
    }
}
}
