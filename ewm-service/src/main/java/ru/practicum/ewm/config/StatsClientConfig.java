package ru.practicum.ewm.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.stat_svc.StatsClient;

@Configuration
public class StatsClientConfig {

    @Value("${server.stats.url}")
    private String statsUrl;

    @Bean
    public StatsClient createStatsClient() {
        return new StatsClient(statsUrl);
    }
}
