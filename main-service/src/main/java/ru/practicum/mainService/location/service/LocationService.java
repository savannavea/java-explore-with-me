package ru.practicum.mainService.location.service;

import ru.practicum.mainService.location.dto.NewLocationDto;
import ru.practicum.mainService.location.dto.UpdateLocation;
import ru.practicum.mainService.request.dto.LocationResponseDto;

import java.util.List;

public interface LocationService {

    LocationResponseDto createLocation(NewLocationDto newLocationDto);

    LocationResponseDto updateLocation(Long locId, UpdateLocation updateLocation);

    List<LocationResponseDto> getAllLocations(Integer from, Integer size);

    LocationResponseDto getLocationById(Long locId);

    void deleteLocationById(Long locId);
}
