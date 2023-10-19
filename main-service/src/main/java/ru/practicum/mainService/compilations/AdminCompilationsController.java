package ru.practicum.mainService.compilations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainService.compilations.dto.CompilationDto;
import ru.practicum.mainService.compilations.dto.NewCompilationDto;
import ru.practicum.mainService.compilations.dto.UpdateCompilationRequest;
import ru.practicum.mainService.compilations.service.CompilationService;

import javax.validation.Valid;

@Validated
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
public class AdminCompilationsController {
    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createCompilation(@RequestBody(required = false)
                                            @Valid NewCompilationDto newCompilationDto) {
        return compilationService.createCompilation(newCompilationDto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compId) {
        compilationService.deleteCompilation(compId);
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateCompilation(@PathVariable Long compId,
                                            @RequestBody @Valid
                                            UpdateCompilationRequest updateCompilationRequest) {
        return compilationService.updateCompilation(compId, updateCompilationRequest);
    }
}