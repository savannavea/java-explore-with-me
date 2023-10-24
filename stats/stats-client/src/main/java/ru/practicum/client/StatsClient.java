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

    private static final String API_PREFIX_HIT = "/hit";
    private static final String API_PREFIX_START = "/stats";


    @Autowired
    public StatsClient(@Value("${stats-service.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> create(HitRequestDto hitRequestDto) {
        return post(API_PREFIX_HIT, hitRequestDto);
    }

    public List<HitResponseDto> getStatistic(LocalDateTime start, LocalDateTime end,
                                             List<String> uris, Boolean unique) {

        Map<String, Object> parameters = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        parameters.put("start", start.format(formatter));
        parameters.put("end", end.format(formatter));
        parameters.put("uris", String.join(",", uris));
        parameters.put("unique", unique);
        var query = "?start={start}&end={end}&uris={uris}&unique={unique}";
        var view = get(API_PREFIX_START + query, parameters);
        ResponseEntity<List<HitResponseDto>> response = restTemplate.exchange(API_PREFIX_START + query,
                HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {
                }, parameters);
        List<HitResponseDto> result = response.getBody();

        return result;

    }
}