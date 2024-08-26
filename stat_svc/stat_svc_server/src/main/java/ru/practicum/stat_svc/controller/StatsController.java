package ru.practicum.stat_svc.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.stat_svc.HitDto;
import ru.practicum.stat_svc.service.StatService;

@RestController
@RequiredArgsConstructor
public class StatsController {

    private final StatService statService;

    @PostMapping("/hit")
    public HitDto hit(@RequestBody HitDto body) {

    }
}
