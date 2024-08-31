package ru.practicum.stat_svc.repositiry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.stat_svc.ViewStats;
import ru.practicum.stat_svc.entity.Stats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<Stats, Long> {

    @Query(value = "SELECT new ru.practicum.stat_svc.ViewStats(s.app, s.uri, count(s.ip)) " +
            "FROM Stats AS s " +
            "WHERE timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY count(s.ip) DESC")
    @Transactional
    List<ViewStats> getAll(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query(value = "SELECT new ru.practicum.stat_svc.ViewStats(s.app, s.uri, count(DISTINCT s.ip)) " +
            "FROM Stats AS s " +
            "WHERE timestamp BETWEEN ?1 AND ?2 " +
            "AND s.uri in ?3 " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY count(s.ip) DESC")
    @Transactional(readOnly = true)
    List<ViewStats> getStatsUnique(LocalDateTime start, LocalDateTime end, List<String> uris);
}