package ru.practicum.mainService.compilations.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.mainService.compilations.dto.CompilationDto;
import ru.practicum.mainService.compilations.dto.NewCompilationDto;
import ru.practicum.mainService.compilations.model.Compilation;
import ru.practicum.mainService.event.dto.EventShortDto;
import ru.practicum.mainService.event.model.Event;

import java.util.List;

@UtilityClass
public class CompilationMapper {
    public static Compilation toCompilation(NewCompilationDto newCompilationDto, List<Event> events) {
        return Compilation.builder()
                .pinned(newCompilationDto.getPinned())
                .title(newCompilationDto.getTitle())
                .events(events)
                .build();

    }

    public static CompilationDto toCompilationDto(Compilation compilation, List<EventShortDto> events) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .events(events)
                .build();

    }
}