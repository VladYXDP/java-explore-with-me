package ru.practicum.ewm.compilations.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilations.entity.Compilation;
import ru.practicum.ewm.compilations.repository.CompilationRepository;
import ru.practicum.ewm.events.entity.Event;
import ru.practicum.ewm.events.repository.EventRepository;
import ru.practicum.ewm.requests.dto.ConfirmedRequestDto;
import ru.practicum.ewm.requests.repository.RequestRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.ewm.requests.enums.RequestStatus.CONFIRMED;

@Service
@Transactional
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;

    @Override
    public Compilation addCompilation(Compilation compilation) {
        if (compilation.getEvents() != null) {
            compilation.setEvents(eventRepository.findAllByIdIn(compilation.getEvents().stream().map(Event::getId).toList()));
        }
        if (compilation.getEvents() != null) {
            List<Long> ids = compilation.getEvents().stream().map(Event::getId).toList();
            Map<Long, Long> confirmedRequests = requestRepository.findAllByEventIdInAndStatus(ids, CONFIRMED)
                    .stream()
                    .collect(Collectors.toMap(ConfirmedRequestDto::getEvent, ConfirmedRequestDto::getCount));
            compilation.setEvents(compilation.getEvents().stream()
                    .map(event -> EventMapper.toEventShortDto(event, confirmedRequests.get(event.getId())))
                    .collect(Collectors.toList()));
        }
        return compilationDto;
    }

    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilation) {
        Compilation compilation = getCompilation(compId);
        if (updateCompilation.getEvents() != null) {
            Set<Event> events = updateCompilation.getEvents().stream().map(id -> {
                Event event = new Event();
                event.setId(id);
                return event;
            }).collect(Collectors.toSet());
            compilation.setEvents(events);
        }
        if (updateCompilation.getPinned() != null) {
            compilation.setPinned(updateCompilation.getPinned());
        }
        String title = updateCompilation.getTitle();
        if (title != null && !title.isBlank()) {
            compilation.setTitle(title);
        }
        CompilationDto compilationDto = CompilationMapper.toCompilationDto(compilationRepository.save(compilation));
        if (compilation.getEvents() != null) {
            List<Long> ids = compilation.getEvents().stream().map(Event::getId).collect(Collectors.toList());
            Map<Long, Long> confirmedRequests = requestRepository.findAllByEventIdInAndStatus(ids, CONFIRMED)
                    .stream()
                    .collect(Collectors.toMap(ConfirmedRequests::getEvent, ConfirmedRequests::getCount));
            compilationDto.setEvents(compilation.getEvents().stream()
                    .map(event -> EventMapper.toEventShortDto(event, confirmedRequests.get(event.getId())))
                    .collect(Collectors.toList()));
        }
        return compilationDto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        if (pinned != null) {
            List<Compilation> compilations = compilationRepository.findAllByPinned(pinned, pageable);
            List<CompilationDto> result = new ArrayList<>();
            for (Compilation compilation : compilations) {
                CompilationDto compilationDto = CompilationMapper.toCompilationDto(compilation);
                if (compilation.getEvents() != null) {
                    List<Long> ids = compilation.getEvents().stream().map(Event::getId).collect(Collectors.toList());
                    Map<Long, Long> confirmedRequests = requestRepository.findAllByEventIdInAndStatus(ids, CONFIRMED)
                            .stream()
                            .collect(Collectors.toMap(ConfirmedRequests::getEvent, ConfirmedRequests::getCount));
                    compilationDto.setEvents(compilation.getEvents().stream()
                            .map(event -> EventMapper.toEventShortDto(event, confirmedRequests.get(event.getId())))
                            .collect(Collectors.toList()));
                }
                result.add(compilationDto);
            }
            return result;
        } else {
            List<Compilation> compilations = compilationRepository.findAll(pageable).getContent();
            List<CompilationDto> result = new ArrayList<>();
            for (Compilation compilation : compilations) {
                CompilationDto compilationDto = CompilationMapper.toCompilationDto(compilation);
                if (compilation.getEvents() != null) {
                    List<Long> ids = compilation.getEvents().stream().map(Event::getId).collect(Collectors.toList());
                    Map<Long, Long> confirmedRequests = requestRepository.findAllByEventIdInAndStatus(ids, CONFIRMED)
                            .stream()
                            .collect(Collectors.toMap(ConfirmedRequests::getEvent, ConfirmedRequests::getCount));
                    compilationDto.setEvents(compilation.getEvents().stream()
                            .map(event -> EventMapper.toEventShortDto(event, confirmedRequests.get(event.getId())))
                            .collect(Collectors.toList()));
                }
                result.add(compilationDto);
            }
            return result;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public CompilationDto getCompilationById(Long compilationId) {
        Compilation compilation = getCompilation(compilationId);
        CompilationDto compilationDto = CompilationMapper.toCompilationDto(compilation);
        if (compilation.getEvents() != null) {
            List<Long> ids = compilation.getEvents().stream().map(Event::getId).collect(Collectors.toList());
            Map<Long, Long> confirmedRequests = requestRepository.findAllByEventIdInAndStatus(ids, CONFIRMED)
                    .stream()
                    .collect(Collectors.toMap(ConfirmedRequests::getEvent, ConfirmedRequests::getCount));
            compilationDto.setEvents(compilation.getEvents().stream()
                    .map(event -> EventMapper.toEventShortDto(event, confirmedRequests.get(event.getId())))
                    .collect(Collectors.toList()));
        }
        return compilationDto;
    }

    @Override
    public void deleteCompilation(Long compilationId) {
        getCompilation(compilationId);
        compilationRepository.deleteById(compilationId);
    }

    private Compilation getCompilation(Long compilationId) {
        return compilationRepository.findById(compilationId).orElseThrow(() ->
                new NotFoundException("Compilation id=" + compilationId + " not found"));
    }

}
