package ru.practicum.stat_svc.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stat_svc.HitDto;
import ru.practicum.stat_svc.StatsDtoResp;
import ru.practicum.stat_svc.entity.Stats;
import ru.practicum.stat_svc.mapping.HitDtoMapping;
import ru.practicum.stat_svc.mapping.StatsDtoMapping;
import ru.practicum.stat_svc.service.StatsService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statService;
    private final HitDtoMapping hitDtoMapping;
    private final StatsDtoMapping statsDtoMapping;

    @PostMapping("/hit")
    public void hit(@RequestBody HitDto body) {
        statService.addStats(hitDtoMapping.toStats(body));
    }

    @GetMapping("/stats")
    public StatsDtoResp stats(@RequestParam(value = "start", required = false) String start,
                              @RequestParam(value = "end", required = false) String end,
                              @RequestParam(value = "uris", required = false) String uris,
                              @RequestParam(value = "unique", required = false, defaultValue = "false") Boolean unique) {
        List<Stats> resp = statService.getStats(statsDtoMapping.toStatsDtoReq(start, end, uris, unique));
        return new StatsDtoResp();
    }
}
