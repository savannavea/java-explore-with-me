package ru.practicum.mainService.compilations.dto;

import lombok.*;
import ru.practicum.mainService.event.dto.EventShortDto;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompilationDto {
    private Long id;

    private String title;

    private Boolean pinned;

    private List<EventShortDto> events;
}