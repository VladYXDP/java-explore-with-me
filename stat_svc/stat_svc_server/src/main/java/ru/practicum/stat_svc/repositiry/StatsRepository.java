package ru.practicum.stat_svc.repositiry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.stat_svc.entity.Stats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<Stats, Long> {

    @Query(value = "SELECT s.app, s.uri, count(DISTINCT s.ip) " +
            "FROM stats AS s " +
            "WHERE timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY count(s.ip) DESC", nativeQuery = true)
    List<Stats> getAll(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query(value = "SELECT s.app, s.uri, count(DISTINCT s.ip) " +
            "FROM stats AS s " +
            "WHERE timestamp BETWEEN ?1 AND ?2 " +
            "AND s.uri in ?3 " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY count(s.ip) DESC", nativeQuery = true)
    List<Stats> getStatsUnique(LocalDateTime start, LocalDateTime end, List<String> uris);
}