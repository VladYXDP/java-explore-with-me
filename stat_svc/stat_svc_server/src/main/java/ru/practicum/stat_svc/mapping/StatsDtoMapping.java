package ru.practicum.stat_svc.mapping;

import org.springframework.stereotype.Component;
import ru.practicum.stat_svc.StatsDtoReq;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

@Component
public class StatsDtoMapping {

    public StatsDtoReq toStatsDtoReq(String start, String end, String uris, Boolean unique) {
        return StatsDtoReq.builder()
                .start(LocalDateTime.parse(start))
                .end(LocalDateTime.parse(end))
                .uris(new ArrayList<>(Arrays.asList(uris.split(","))))
                .unique(unique)
                .build();
    }
}
