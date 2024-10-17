package ru.practicum.stat_svc;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class StatsClient {

    private final String serverUrl;
    private final RestTemplate rest;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public StatsClient(String serverUrl) {
        this.serverUrl = serverUrl;
        this.rest = new RestTemplate();
    }

    public void saveHit(HitDto hit) {
        ResponseEntity<Object> response;
        try {
            response = rest.postForEntity(serverUrl + "/hit", hit, Object.class);
        } catch (HttpStatusCodeException e) {
            ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
            return;
        }
        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());
        if (response.hasBody()) {
            responseBuilder.body(response.getBody());
            return;
        }
        responseBuilder.build();
    }

    public ResponseEntity<Object> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        StringBuilder url = new StringBuilder(serverUrl + "/stats?");
        url.append("&uris=");
        for (String uri : uris) {
            url.append(uri);
        }
        url.append("&unique=").append(unique);
        url.append("&start=").append(start.format(formatter));
        url.append("&end=").append(end.format(formatter));

        ResponseEntity<Object> response;
        try {
            response = rest.exchange(url.toString(), HttpMethod.GET, null, Object.class);
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());
        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }
        return responseBuilder.build();
    }
}
