package ru.practicum.stat_svc.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stat_svc.HitDto;
import ru.practicum.stat_svc.StatsDtoReq;
import ru.practicum.stat_svc.StatsDtoResp;
import ru.practicum.stat_svc.mapping.HitDtoMapping;
import ru.practicum.stat_svc.service.StatsService;

@RestController
@RequestMapping("/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statService;
    private final HitDtoMapping hitDtoMapping;

    @PostMapping("/hit")
    public void hit(@RequestBody HitDto body, HttpServletResponse response) {
        statService.addStats(hitDtoMapping.toStats(body));
        response.setStatus(HttpServletResponse.SC_CREATED);
    }

    @GetMapping("/stats")
    public StatsDtoResp stats(@RequestBody StatsDtoReq stats) {
        return new StatsDtoResp();
    }
}
