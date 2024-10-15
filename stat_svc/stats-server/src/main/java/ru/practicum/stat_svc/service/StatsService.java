package ru.practicum.stat_svc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.stat_svc.ViewStats;
import ru.practicum.stat_svc.entity.Stats;
import ru.practicum.stat_svc.repositiry.StatsRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class StatsService {

    private final StatsRepository statsRepository;

    public void addStats(Stats hit) {
        statsRepository.save(hit);
    }

    @Transactional(readOnly = true)
    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        List<ViewStats> result;
        if (unique) {
            if (uris != null) {
                result = statsRepository.findHitsWithUniqueIpWithUris(uris, start, end);
            } else {
                result = statsRepository.findHitsWithUniqueIpWithoutUris(start, end);
            }
        } else {
            if (uris != null) {
                result = statsRepository.findAllHitsWithUris(uris, start, end);
            } else {
                result = statsRepository.findAllHitsWithoutUris(start, end);
            }
        }
        return result;
    }
}
