package ru.practicum.mainService.location.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.mainService.location.dto.LocationDto;
import ru.practicum.mainService.location.mapper.LocationMapper;
import ru.practicum.mainService.location.repository.LocationRepository;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;

    @Override
    public LocationDto createLocation(LocationDto locationDto) {

        return LocationMapper.toLocationDto(locationRepository.save(LocationMapper.toLocation(locationDto)));

    }
}
