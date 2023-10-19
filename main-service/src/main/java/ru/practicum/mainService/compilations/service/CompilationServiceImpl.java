package ru.practicum.mainService.compilations.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.mainService.category.mapper.CategoryMapper;
import ru.practicum.mainService.compilations.dto.CompilationDto;
import ru.practicum.mainService.compilations.dto.NewCompilationDto;
import ru.practicum.mainService.compilations.dto.UpdateCompilationRequest;
import ru.practicum.mainService.compilations.mapper.CompilationMapper;
import ru.practicum.mainService.compilations.model.Compilation;
import ru.practicum.mainService.compilations.repository.CompilationRepository;
import ru.practicum.mainService.event.dto.EventShortDto;
import ru.practicum.mainService.event.mapper.EventMapper;
import ru.practicum.mainService.event.model.Event;
import ru.practicum.mainService.event.repository.EventRepository;
import ru.practicum.mainService.exception.NotFoundException;
import ru.practicum.mainService.user.mapper.UserMapper;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        int offset = from > 0 ? from / size : 0;
        PageRequest page = PageRequest.of(offset, size);
        List<Compilation> compilations;

        if (pinned == null) {
            compilations = compilationRepository.findAll(page).getContent();
        } else {
            compilations = compilationRepository.findAllByPinned(pinned, page);
        }
        if (compilations.isEmpty()) {
            return Collections.emptyList();
        }
        return compilations
                .stream()
                .map(compilation ->
                        CompilationMapper.toCompilationDto(compilation,
                                toCompilationDtoList(compilation.getEvents()))).collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        Compilation compilation = getCompilationOrElseThrow(compId);
        return CompilationMapper.toCompilationDto(compilation, compilation.getEvents()
                .stream()
                .map(e -> EventMapper.toEventShortDto(e,
                        CategoryMapper.toCategoryDto(e.getCategory()),
                        UserMapper.toUserShortDto(e.getInitiator())))
                .collect(Collectors.toList()));
    }

    @Override
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        List<Event> events = new ArrayList<>();

        if (!Objects.isNull(newCompilationDto.getEvents())) {
            events = eventRepository.getByIdIn(newCompilationDto.getEvents());
        }

        Compilation compilation = CompilationMapper.toCompilation(newCompilationDto, events);

        Compilation savedCompilation = compilationRepository.save(compilation);

        return CompilationMapper.toCompilationDto(savedCompilation, events
                .stream()
                .map(e -> EventMapper.toEventShortDto(e,
                        CategoryMapper.toCategoryDto(e.getCategory()),
                        UserMapper.toUserShortDto(e.getInitiator())))
                .collect(Collectors.toList()));
    }

    @Override
    public void deleteCompilation(Long compId) {
        getCompilationOrElseThrow(compId);
        compilationRepository.deleteById(compId);

    }

    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = getCompilationOrElseThrow(compId);
        List<Event> events = new ArrayList<>();

        if (!Objects.isNull(updateCompilationRequest.getEvents())) {
            events = eventRepository.getByIdIn(updateCompilationRequest.getEvents());
        }
        compilation.setEvents(events);

        if (!Objects.isNull(updateCompilationRequest.getTitle())) {
            compilation.setTitle(updateCompilationRequest.getTitle());
        }

        if (!Objects.isNull(updateCompilationRequest.getPinned())) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        }

        Compilation savedCompilation = compilationRepository.save(compilation);

        return CompilationMapper.toCompilationDto(savedCompilation, events
                .stream()
                .map(e -> EventMapper.toEventShortDto(e,
                        CategoryMapper.toCategoryDto(e.getCategory()),
                        UserMapper.toUserShortDto(e.getInitiator())))
                .collect(Collectors.toList()));
    }

    private List<EventShortDto> toCompilationDtoList(List<Event> events) {
        return events
                .stream()
                .map(event -> EventMapper.toEventShortDto(
                        event,
                        CategoryMapper.toCategoryDto(event.getCategory()),
                        UserMapper.toUserShortDto(event.getInitiator())
                ))
                .collect(Collectors.toList());
    }

    private Compilation getCompilationOrElseThrow(Long compId) {
        return compilationRepository
                .findById(compId)
                .orElseThrow(() -> new NotFoundException("There is no such collection."));
    }
}