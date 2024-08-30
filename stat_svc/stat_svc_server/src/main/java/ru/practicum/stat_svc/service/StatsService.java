package ru.practicum.stat_svc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.stat_svc.StatsDtoReq;
import ru.practicum.stat_svc.ViewStats;
import ru.practicum.stat_svc.entity.Stats;
import ru.practicum.stat_svc.repositiry.StatsRepository;

import javax.swing.text.View;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final StatsRepository statsRepository;

    public void addStats(Stats hit) {
        statsRepository.save(hit);
    }

    public List<ViewStats> getStats(StatsDtoReq statsDtoReq) {
        List<ViewStats> stats;
        if (statsDtoReq.isUnique()) {
            stats = statsRepository.getAll(statsDtoReq.getStart(), statsDtoReq.getEnd(), statsDtoReq.getUris());
        } else {
            stats = statsRepository.getStatsUnique(statsDtoReq.getStart(), statsDtoReq.getEnd(), statsDtoReq.getUris());
        }
        return stats;
    }
}
