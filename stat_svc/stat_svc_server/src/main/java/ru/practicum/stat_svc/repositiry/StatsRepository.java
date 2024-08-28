package ru.practicum.stat_svc.repositiry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.stat_svc.entity.Stats;

import java.util.List;

public interface StatsRepository extends JpaRepository<Stats, Long> {

    @Query(value = "SELECT s.id, s.app, s.ip, s.timestamp, s.uri FROM stats AS s WHERE s.ip like ?1 AND timestamp >= ?2 AND timestamp <= ?3", nativeQuery = true)
    List<Stats> getStats(String ip);
}