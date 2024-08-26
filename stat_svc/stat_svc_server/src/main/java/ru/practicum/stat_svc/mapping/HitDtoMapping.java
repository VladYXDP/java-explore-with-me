package ru.practicum.stat_svc.mapping;

import org.springframework.stereotype.Component;
import ru.practicum.stat_svc.HitDto;
import ru.practicum.stat_svc.entity.Hit;

@Component
public class HitDtoMapping {

    public Hit toHit(HitDto dto) {
        return Hit.builder()
                .uri(dto.getUri())
                .app(dto.getApp())
                .ip(dto.getIp())
                .timestamp(dto.getTimestamp())
                .build();
    }
}
