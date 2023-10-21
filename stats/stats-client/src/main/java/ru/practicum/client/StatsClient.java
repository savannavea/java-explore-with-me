package ru.practicum.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.statsDto.HitRequestDto;
import ru.practicum.statsDto.HitResponseDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatsClient extends BaseClient {

    @Autowired
    public StatsClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> create(HitRequestDto hitRequestDto) {
        return post("/hit", hitRequestDto);
    }


    public List<HitResponseDto> getStatistic(LocalDateTime start, LocalDateTime end,
                                             List<String> uris, Boolean unique) {

        Map<String, Object> parameters = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        parameters.put("start", start.format(formatter));
        parameters.put("end", end.format(formatter));
        parameters.put("uris", String.join(",", uris));
        parameters.put("unique", unique);
        String query = "?start={start}&end={end}&uris={uris}&unique={unique}";
        get("/hit" + query, parameters);
        ResponseEntity<List<HitResponseDto>> response = restTemplate.exchange("/hit" + query,
                HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {
                }, parameters);

        return response.getBody();
    }
}