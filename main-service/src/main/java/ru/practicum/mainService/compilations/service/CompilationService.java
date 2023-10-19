package ru.practicum.mainService.compilations.service;

import ru.practicum.mainService.compilations.dto.CompilationDto;
import ru.practicum.mainService.compilations.dto.NewCompilationDto;
import ru.practicum.mainService.compilations.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size);

    CompilationDto getCompilationById(Long compId);

    CompilationDto createCompilation(NewCompilationDto newCompilationDto);

    void deleteCompilation(Long compId);

    CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest);
}