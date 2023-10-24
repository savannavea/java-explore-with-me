package ru.practicum.mainService.location.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.mainService.location.dto.LocationDto;
import ru.practicum.mainService.location.model.Location;

@UtilityClass
public class LocationMapper {
    public static LocationDto toLocationDto(Location location) {
        return LocationDto.builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }

    public static Location toLocation(LocationDto locationDto) {
        return Location.builder()
                .lat(locationDto.getLat())
                .lon(locationDto.getLon())
                .build();
    }
}