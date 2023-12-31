package ru.practicum.mainService.compilations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainService.compilations.dto.CompilationDto;
import ru.practicum.mainService.compilations.service.CompilationService;

import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/compilations")
public class PublicCompilationsController {
    private final CompilationService compilationsService;

    @GetMapping
    public List<CompilationDto> getAllCompilations(@RequestParam(required = false) Boolean pinned,
                                                   @RequestParam(required = false, defaultValue = "0")
                                                   @PositiveOrZero Integer from,
                                                   @RequestParam(required = false, defaultValue = "10")
                                                   @PositiveOrZero Integer size) {
        return compilationsService.getCompilations(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilationsById(@PathVariable Long compId) {
        return compilationsService.getCompilationById(compId);
    }
}