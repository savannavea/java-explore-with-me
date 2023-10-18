package ru.practicum.location.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.location.dto.LocationDto;
import ru.practicum.location.mapper.LocationMapper;
import ru.practicum.location.repository.LocationRepository;

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
