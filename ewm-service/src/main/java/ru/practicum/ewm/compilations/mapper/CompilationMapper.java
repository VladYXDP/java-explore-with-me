package ru.practicum.ewm.compilations.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.compilations.dto.CompilationDto;
import ru.practicum.ewm.compilations.dto.CreateCompilationDto;
import ru.practicum.ewm.compilations.entity.Compilation;
import ru.practicum.ewm.events.mapper.EventMapper;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CompilationMapper {

    private final EventMapper eventMapper;

    public Compilation toCompilation(CreateCompilationDto dto) {
        return new Compilation(
                dto.getTitle(),
                dto.isPinned()
        );
    }

    public Compilation toCompilation(CompilationDto dto) {
        return new Compilation(
                dto.getId(), dto.getTitle(), dto.getPinned(), eventMapper.toEvent(dto.getEvents())
        );
    }

    public List<CompilationDto> toCompilationDto(List<Compilation> compilation) {
        return compilation.stream().map(this::toCompilationDto).toList();
    }

    public CompilationDto toCompilationDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .events(eventMapper.toEventDto(compilation.getEvents()))
                .pinned(compilation.getPinned())
                .build();
    }
}
