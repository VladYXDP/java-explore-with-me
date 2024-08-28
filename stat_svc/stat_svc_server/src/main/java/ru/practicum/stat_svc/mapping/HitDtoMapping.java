package ru.practicum.stat_svc.mapping;

import org.springframework.stereotype.Component;
import ru.practicum.stat_svc.HitDto;
import ru.practicum.stat_svc.entity.Stats;

@Component
public class HitDtoMapping {

    public Stats toStats(HitDto dto) {
        return Stats.builder()
                .uri(dto.getUri())
                .app(dto.getApp())
                .ip(dto.getIp())
                .timestamp(dto.getTimestamp())
                .build();
    }

    public HitDto hitDto(Stats stats) {
        return new HitDto(stats.getId(), stats.getApp(), stats.getUri(), stats.getIp(), stats.getTimestamp());
    }
}
