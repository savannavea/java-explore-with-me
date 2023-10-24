package ru.practicum.mainService.location.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationDto {
    private float lat;

    private float lon;
}