package ru.practicum.stat_svc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StatsDtoResp {
    private String app;
    private String uri;
    private List<HitDto> hits;
}
