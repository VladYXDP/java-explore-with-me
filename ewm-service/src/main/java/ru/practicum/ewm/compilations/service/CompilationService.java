package ru.practicum.ewm.compilations.service;

import ru.practicum.ewm.compilations.entity.Compilation;

import java.util.List;

public interface CompilationService {

    Compilation addCompilation(Compilation compilation);

    Compilation updateCompilation(Long compId, Compilation compilation);

    List<Compilation> getCompilations(Boolean pinned, Integer from, Integer size);

    Compilation getCompilationById(Long compilationId);

    void deleteCompilation(Long compilationId);
}
