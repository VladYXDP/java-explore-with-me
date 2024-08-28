package ru.practicum.stat_svc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.stat_svc.StatsDtoReq;
import ru.practicum.stat_svc.StatsDtoResp;
import ru.practicum.stat_svc.entity.Stats;
import ru.practicum.stat_svc.repositiry.StatsRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final StatsRepository statsRepository;

    public void addStats(Stats hit) {
        statsRepository.save(hit);
    }

    public StatsDtoResp getStats(StatsDtoReq statsReq) {
        List<Stats> stats;
        return null;
    }
}
