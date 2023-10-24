package ru.practicum.mainService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import ru.practicum.client.StatsClient;
import ru.practicum.mainService.event.dto.EventFullDto;
import ru.practicum.mainService.event.dto.EventShortDto;
import ru.practicum.statsDto.HitRequestDto;
import ru.practicum.statsDto.HitResponseDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class StatisticClient {
    private static final String APP = "main-service";
    private static final int YEARS_OFFSET = 100;

    private final StatsClient statsClient;

    public ResponseEntity<Object> saveHit(String uri, String ip) {
        HitRequestDto hitRequestDto = HitRequestDto.builder()
                .app(APP)
                .uri(uri)
                .ip(ip)
                .timestamp(LocalDateTime.now())
                .build();
        return statsClient.create(hitRequestDto);
    }

    public EventFullDto setViewsNumber(EventFullDto event) {
        List<HitResponseDto> hits = statsClient.getStatistic(event.getCreatedOn(), LocalDateTime.now(),
                List.of("/events/" + event.getId()), true);
        if (!hits.isEmpty()) {
            event.setViews(hits.get(0).getHits());
        } else {
            event.setViews(0L);
        }
        return event;
    }

    public List<EventShortDto> setViewsNumber(List<EventShortDto> events) {
        List<String> uris = new ArrayList<>();
        for (EventShortDto eventShortDto : events) {
            uris.add("/events/" + eventShortDto.getId());
        }

        List<HitResponseDto> hits = statsClient.getStatistic(LocalDateTime.now().minusYears(YEARS_OFFSET),
                LocalDateTime.now(), uris, true);
        if (!hits.isEmpty()) {
            Map<Long, Integer> hitMap = mapHits(hits);
            for (EventShortDto event : events) {
                event.setViews(hitMap.getOrDefault(event.getId(), 0));
            }
        } else {
            for (EventShortDto event : events) {
                event.setViews(0);
            }
        }
        return events;
    }

    private Map<Long, Integer> mapHits(List<HitResponseDto> hits) {
        Map<Long, Integer> hitMap = new HashMap<>();
        for (HitResponseDto hit : hits) {
            String hitUri = hit.getUri();
            Long id = Long.valueOf(hitUri.substring(8));
            hitMap.put(id, (int) hit.getHits());
        }
        return hitMap;
    }
}
