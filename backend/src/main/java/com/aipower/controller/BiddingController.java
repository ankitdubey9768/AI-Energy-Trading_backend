package com.aipower.controller;

import com.aipower.service.MlServiceClient;
import com.aipower.service.RiskService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // For prototyping
public class BiddingController {
    
    private final MlServiceClient mlServiceClient;
    private final RiskService riskService;
    
    public BiddingController(MlServiceClient mlServiceClient, RiskService riskService) {
        this.mlServiceClient = mlServiceClient;
        this.riskService = riskService;
    }
    
    @GetMapping("/forecast")
    public Object getForecast(@RequestParam String market, @RequestParam String date) {
        System.out.println("market: "+market+ "date: "+date);
        return mlServiceClient.getForecast(market, date);
    }
    
    @PostMapping("/recommend")
    public Object getRecommendations(@RequestBody Map<String, Object> request) {
        List<Map<String, Object>> forecastData = (List<Map<String, Object>>) request.get("forecast_data");
        String strategy = (String) request.get("strategy");
        return mlServiceClient.getRecommendations(forecastData, strategy);
    }
    
    @PostMapping("/risk")
    public Object calculateRisk(@RequestBody Map<String, Object> request) {
        List<Map<String, Object>> currentBids = (List<Map<String, Object>>) request.get("bids");
        return riskService.calculateRiskMetrics(currentBids);
    }
}
