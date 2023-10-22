package ru.practicum.server.mapper;
import lombok.experimental.UtilityClass;
import ru.practicum.server.model.Stat;
import ru.practicum.statsDto.HitRequestDto;

@UtilityClass
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
                .ip(hitRequestDto.getIp())
                .timestamp(hitRequestDto.getTimestamp())
                .uri(hitRequestDto.getUri())
                .build();
    }
}
