package ru.practicum.ewm.compilations.mapper;

import lombok.RequiredArgsConstructor;
import org.hibernate.sql.Update;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.compilations.dto.CompilationDto;
import ru.practicum.ewm.compilations.dto.CreateCompilationDto;
import ru.practicum.ewm.compilations.dto.UpdateCompilationDto;
import ru.practicum.ewm.compilations.entity.Compilation;
import ru.practicum.ewm.events.mapper.EventMapper;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CompilationMapper {

    private final EventMapper eventMapper;

    public Compilation toCompilation(CreateCompilationDto dto) {
        return Compilation.builder()
                .title(dto.getTitle())
                .eventsId(dto.getEvents())
                .pinned(dto.isPinned())
                .build();
    }

    public Compilation toCompilation(UpdateCompilationDto dto) {
        return Compilation.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .pinned(dto.isPinned())
                .eventsId(dto.getEvents())
                .build();
    }

    public List<CompilationDto> toCompilationDto(List<Compilation> compilation) {
        return compilation.stream().map(this::toCompilationDto).toList();
    }

    public CompilationDto toCompilationDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .events(eventMapper.toEventShortDto(compilation.getEvents()))
                .pinned(compilation.getPinned())
                .build();
    }
}
