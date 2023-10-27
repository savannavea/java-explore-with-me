package ru.practicum.mainService.location.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.mainService.location.dto.LocationDto;
import ru.practicum.mainService.location.dto.NewLocationDto;
import ru.practicum.mainService.location.model.Location;
import ru.practicum.mainService.request.dto.LocationResponseDto;

@UtilityClass
public class LocationMapper {
    public static LocationDto toLocationDto(Location location) {
        return LocationDto.builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }

    public static Location toLocation(NewLocationDto newLocationDto) {
        return Location.builder()
                .name(newLocationDto.getName())
                .lat(newLocationDto.getLat())
                .lon(newLocationDto.getLon())
                .radius(newLocationDto.getRadius())
                .build();
    }

    public LocationResponseDto toLocationResponseDto(Location location) {
        return LocationResponseDto.builder()
                .id(location.getId())
                .name(location.getName())
                .lat(location.getLat())
                .lon(location.getLon())
                .radius(location.getRadius())
                .locationState(location.getStatus())
                .build();
    }
}