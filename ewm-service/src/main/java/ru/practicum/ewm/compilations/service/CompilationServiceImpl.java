package ru.practicum.ewm.compilations.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilations.entity.Compilation;
import ru.practicum.ewm.compilations.repository.CompilationRepository;
import ru.practicum.ewm.events.entity.Event;
import ru.practicum.ewm.events.repository.EventRepository;
import ru.practicum.ewm.exceptions.NotFoundException;
import ru.practicum.ewm.requests.entity.Request;
import ru.practicum.ewm.requests.repository.RequestRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.ewm.requests.enums.RequestStatus.CONFIRMED;

@Service
@Transactional
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final CompilationRepository compilationRepository;

    @Override
    public Compilation addCompilation(Compilation compilation) {
        List<Event> events = null;
        if (compilation.getEvents() != null) {
            events = eventRepository.findAllByIdIn(compilation.getEvents().stream().map(Event::getId).toList());
        }
        if (events != null) {
            compilation.setEvents(events);
            List<Long> ids = compilation.getEvents().stream().map(Event::getId).toList();
            Map<Long, Long> confirmedRequests = requestRepository.findAllByEventIdInAndStatus(ids, CONFIRMED)
                    .stream()
                    .collect(Collectors.toMap(Request::getEventId, Request::getCount));
            events.forEach(event -> event.setConfirmedRequest(confirmedRequests.get(event.getId())));
        }
        return compilation;
    }

    @Override
    public Compilation updateCompilation(Long compId, Compilation compilation) {
        Compilation currentCompilation = getCompilation(compId);
        if (compilation.getEvents() != null) {
            currentCompilation.setEvents(compilation.getEvents());
        }
        if (compilation.getPinned() != null) {
            currentCompilation.setPinned(compilation.getPinned());
        }
        String title = compilation.getTitle();
        if (title != null && !title.isBlank()) {
            currentCompilation.setTitle(title);
        }
        if(currentCompilation.getEvents() != null) {
            List<Long> ids = currentCompilation.getEvents().stream().map(Event::getId).toList();
            Map<Long, Long> confirmedRequests = requestRepository.findAllByEventIdInAndStatus(ids, CONFIRMED)
                    .stream()
                    .collect(Collectors.toMap(Request::getCount, Request::getEventId));
            currentCompilation.getEvents().forEach(event -> event.setConfirmedRequest(confirmedRequests.get(event.getConfirmedRequest())));
        }
        return compilationRepository.save(currentCompilation);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Compilation> getCompilations(Boolean pinned, Integer from, Integer size) {
        List<Compilation> compilations = new ArrayList<>();
        Pageable pageable = PageRequest.of(from / size, size);
        if (pinned != null) {
            compilations = compilationRepository.findAllByPinned(pinned, pageable);
            compilations.forEach(compilation -> {
                if (compilation.getEvents() != null) {
                    List<Long> ids = compilation.getEvents().stream().map(Event::getId).toList();
                    Map<Long, Long> confirmedRequest = requestRepository.findAllByEventIdInAndStatus(ids, CONFIRMED)
                            .stream()
                            .collect(Collectors.toMap(Request::getCount, Request::getEventId));
                }
            });
            return compilations;
        } else {
            compilations = compilationRepository.findAll(pageable).getContent();
            compilations.forEach(compilation -> {
                if (compilation.getEvents() != null) {
                    List<Long> ids = compilation.getEvents().stream().map(Event::getId).toList();
                    Map<Long, Long> confirmedRequests = requestRepository.findAllByEventIdInAndStatus(ids, CONFIRMED)
                            .stream()
                            .collect(Collectors.toMap(Request::getCount, Request::getEventId));
                    compilation.getEvents().forEach(event -> event.setConfirmedRequest(confirmedRequests.get(event.getId())));
                }
            });
            return compilations;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Compilation getCompilationById(Long compilationId) {
        Compilation compilation = getCompilation(compilationId);
        if (compilation.getEvents() != null) {
            List<Long> ids = compilation.getEvents().stream().map(Event::getId).collect(Collectors.toList());
            Map<Long, Long> confirmedRequests = requestRepository.findAllByEventIdInAndStatus(ids, CONFIRMED)
                    .stream()
                    .collect(Collectors.toMap(Request::getCount, Request::getEventId));
            compilation.getEvents().forEach(event -> event.setConfirmedRequest(confirmedRequests.get(event.getId())));
        }
        return compilation;
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
