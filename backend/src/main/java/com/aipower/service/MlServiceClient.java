package com.aipower.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.util.Map;
import java.util.List;

@Service
public class MlServiceClient {
    private final RestClient restClient;
    
    public MlServiceClient() {
        this.restClient = RestClient.create("http://127.0.0.1:8000");
    }
    
    public Object getForecast(String market, String date) {

        return restClient.post()
                .uri("/forecast")
                .body(Map.of("market", market, "date", date))
                .retrieve()
                .body(Object.class);
    }
    
    public Object getRecommendations(List<Map<String, Object>> forecastData, String strategy) {
        return restClient.post()
                .uri("/recommend")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(Map.of("forecast_data", forecastData, "strategy", strategy))
                .retrieve()
                .body(Object.class);
    }
}
