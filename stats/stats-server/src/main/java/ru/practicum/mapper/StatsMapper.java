package ru.practicum.mapper;

import ru.practicum.HitRequestDto;
import ru.practicum.model.Stat;

public class StatsMapper {

    public static HitRequestDto toHitRequestDto(Stat stat) {
        return HitRequestDto.builder()
                .app(stat.getApp())
                .uri(stat.getUri())
                .ip(stat.getIp())
                .timestamp(stat.getTimestamp())
                .build();

    }

    public static Stat toStat(HitRequestDto hitRequestDto) {
        return Stat.builder()
                .app(hitRequestDto.getApp())
                //.id(hitRequestDto.getId())
                .ip(hitRequestDto.getIp())
                .timestamp(hitRequestDto.getTimestamp())
                .uri(hitRequestDto.getUri())
                .build();

    }
}
