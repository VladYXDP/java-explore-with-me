package ru.practicum.stat_svc.repositiry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.stat_svc.ViewStats;
import ru.practicum.stat_svc.entity.Stats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<Stats, Long> {

    @Query("SELECT new ru.practicum.stat_svc.ViewStats(h.app, h.uri, COUNT(DISTINCT h.ip)) " +
            "FROM Stats AS h " +
            "WHERE h.timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(DISTINCT h.ip) DESC")
    List<ViewStats> findHitsWithUniqueIpWithoutUris(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.stat_svc.ViewStats(h.app, h.uri, COUNT(DISTINCT h.ip)) " +
            "FROM Stats AS h " +
            "WHERE h.uri IN (?1) AND h.timestamp BETWEEN ?2 AND ?3 " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(DISTINCT h.ip) DESC")
    List<ViewStats> findHitsWithUniqueIpWithUris(List<String> uris, LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.stat_svc.ViewStats(h.app, h.uri, COUNT(h.uri)) " +
            "FROM Stats AS h " +
            "WHERE h.timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT (h.uri) DESC")
    List<ViewStats> findAllHitsWithoutUris(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.stat_svc.ViewStats(h.app, h.uri, COUNT(h.uri)) " +
            "FROM Stats AS h " +
            "WHERE h.uri IN (?1) AND h.timestamp BETWEEN ?2 AND ?3 " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT (h.uri) DESC")
    List<ViewStats> findAllHitsWithUris(List<String> uris, LocalDateTime start, LocalDateTime end);
}