package ru.practicum.mainService.location.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.mainService.exception.ConflictException;
import ru.practicum.mainService.exception.NotFoundException;
import ru.practicum.mainService.location.dto.NewLocationDto;
import ru.practicum.mainService.location.dto.UpdateLocation;
import ru.practicum.mainService.location.enums.LocationStatus;
import ru.practicum.mainService.location.mapper.LocationMapper;
import ru.practicum.mainService.location.model.Location;
import ru.practicum.mainService.location.repository.LocationRepository;
import ru.practicum.mainService.request.dto.LocationResponseDto;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;

    @Override
    public LocationResponseDto createLocation(NewLocationDto newLocationDto) {

        if (locationRepository.existsLocationByName(newLocationDto.getName())) {
            throw new ConflictException("Such a location already exists");
        }
        if (locationRepository.existsLocationByLatAndLon(newLocationDto.getLat(), newLocationDto.getLon())) {
            throw new ConflictException("Such coordinates already exist");
        }
        Location location = LocationMapper.toLocation(newLocationDto);
        location.setStatus(LocationStatus.APPROVED);
        locationRepository.save(location);

        return LocationMapper.toNewLocationDto(location);
    }

    @Override
    public LocationResponseDto updateLocation(Long locId, UpdateLocation updateLocation) {
        Location location = getLocationOrElseThrow(locId);
        if (updateLocation.getLat() != null) {
            location.setLat(updateLocation.getLat());
        }
        if (updateLocation.getLon() != null) {
            location.setLon(updateLocation.getLon());
        }
        if (updateLocation.getName() != null) {
            location.setName(updateLocation.getName());
        }
        if (updateLocation.getRadius() != null) {
            location.setRadius(updateLocation.getRadius());
        }
        if (updateLocation.getStatus() != null) {
            location.setStatus(updateLocation.getStatus());
        }
        locationRepository.save(location);
        return LocationMapper.toNewLocationDto(location);
    }

    @Override
    public List<LocationResponseDto> getAllLocations(Integer from, Integer size) {
        int offset = from > 0 ? from / size : 0;
        PageRequest page = PageRequest.of(offset, size, Sort.by("id"));
        List<Location> locationList = locationRepository.findAll(page).getContent();

        return locationList
                .stream()
                .map(LocationMapper::toLocationResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public LocationResponseDto getLocationById(Long locId) {
        Location location = getLocationOrElseThrow(locId);
        if (location.getStatus() != LocationStatus.APPROVED) {
            throw new ConflictException("This location is unavailable");
        }
        return LocationMapper.toLocationResponseDto(location);
    }

    @Override
    public void deleteLocationById(Long locId) {
        Location location = getLocationOrElseThrow(locId);
        if (location.getName() == null || location.getRadius() == null) {
            throw new NotFoundException("This location has not been found");
        }
        locationRepository.deleteById(locId);
    }

    private Location getLocationOrElseThrow(Long id) {
        return locationRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("There is no such location"));
    }
}