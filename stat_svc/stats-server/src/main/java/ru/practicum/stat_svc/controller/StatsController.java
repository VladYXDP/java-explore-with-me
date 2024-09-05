package ru.practicum.stat_svc.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stat_svc.HitDto;
import ru.practicum.stat_svc.ViewStats;
import ru.practicum.stat_svc.mapping.HitDtoMapping;
import ru.practicum.stat_svc.service.StatsService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statService;
    private final HitDtoMapping hitDtoMapping;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void hit(@RequestBody @Valid HitDto body) {
        statService.addStats(hitDtoMapping.toStats(body));
    }

    @GetMapping("/stats")
    public List<ViewStats> stats(@RequestParam(value = "start", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                 @RequestParam(value = "end", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                 @RequestParam(value = "uris", required = false) String uris,
                                 @RequestParam(value = "unique", required = false, defaultValue = "false") Boolean unique) {
        return statService.getStats(start, end, Arrays.asList(uris.split(",")), unique);
    }
}
