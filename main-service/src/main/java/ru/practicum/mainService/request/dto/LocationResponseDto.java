package ru.practicum.mainService.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.mainService.location.enums.LocationStatus;

@Data
@Builder
@AllArgsConstructor
public class LocationResponseDto {
    private long id;

    private Float lat;

    private Float lon;

    private String name;

    private Float radius;

    private LocationStatus locationState;
}