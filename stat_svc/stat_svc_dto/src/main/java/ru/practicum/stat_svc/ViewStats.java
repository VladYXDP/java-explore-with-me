package ru.practicum.stat_svc;

import lombok.*;

@Getter
@AllArgsConstructor
public class ViewStats {
    private String app;
    private String uri;
    private Long ipCount;
}
