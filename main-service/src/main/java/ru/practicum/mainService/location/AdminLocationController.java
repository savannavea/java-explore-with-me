package ru.practicum.mainService.location;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainService.location.dto.NewLocationDto;
import ru.practicum.mainService.location.dto.UpdateLocation;
import ru.practicum.mainService.location.service.LocationService;
import ru.practicum.mainService.request.dto.LocationResponseDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/admin/locations")
@RequiredArgsConstructor
public class AdminLocationController {

    private final LocationService locationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LocationResponseDto saveLocation(@Valid @RequestBody NewLocationDto newLocationDto) {
        return locationService.createLocation(newLocationDto);
    }

    @PatchMapping("/{locId}")
    public LocationResponseDto updateLocation(@PathVariable Long locId, @RequestBody UpdateLocation updateLocation) {
        return locationService.updateLocation(locId, updateLocation);

    }

    @GetMapping
    public List<LocationResponseDto> getAllLocations(@PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                                     Integer from,
                                                     @Positive @RequestParam(name = "size", defaultValue = "10")
                                                     Integer size) {
        return locationService.getAllLocations(from, size);
    }

    @GetMapping("/{locId}")
    public LocationResponseDto getLocationById(@PathVariable Long locId) {
        return locationService.getLocationById(locId);
    }

    @DeleteMapping("/{locId}")
    public void deleteLocationById(@PathVariable Long locId) {
        locationService.deleteLocationById(locId);
    }
}