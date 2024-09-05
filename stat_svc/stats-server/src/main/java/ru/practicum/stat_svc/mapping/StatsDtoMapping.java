package ru.practicum.stat_svc.mapping;

import org.springframework.stereotype.Component;
import ru.practicum.stat_svc.StatsDtoReq;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

@Component
public class StatsDtoMapping {

    public StatsDtoReq toStatsDtoReq(LocalDateTime start, LocalDateTime end, String uris, Boolean unique) {
        return StatsDtoReq.builder()
                .start(start)
                .end(end)
                .uris(new ArrayList<>(Arrays.asList(uris.split(","))))
                .unique(unique)
                .build();
    }
}
