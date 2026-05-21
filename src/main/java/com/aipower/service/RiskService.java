package com.aipower.service;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.List;
import java.util.HashMap;

@Service
public class RiskService {

    public Map<String, Object> calculateRiskMetrics(List<Map<String, Object>> bids) {
        // Calculate VaR, worst-case DSM Penalty, and check constraints
        double worstCaseDsmPenalty = 0.0;
        int constraintBreaches = 0;

        for (Map<String, Object> bid : bids) {
            double price = Double.parseDouble(bid.get("bid_price").toString());
            double volume = Double.parseDouble(bid.get("volume_mw").toString());

            // Simple mock penalty calculation logic
            // Assuming 10% deviation leading to 1.5x penalty parameter
            worstCaseDsmPenalty += (price * volume * 0.1 * 1.5);
            // CERC Price Cap (approx 10 INR/kWh)
            // Volume Constraint (125 MW Physical Line Limit): This ensures that during
            // extreme Heatwaves (peak High Demand blocks) or when running 'Aggressive'
            // strategies, the user natively triggers visual infrastructure violations!
            if (volume > 125.0 || price > 10.0) {
                constraintBreaches++;
            }
        }

        double valueAtRisk = worstCaseDsmPenalty * 0.8; // 80% of worst case

        Map<String, Object> result = new HashMap<>();
        result.put("worstCaseDsmPenalty", Math.round(worstCaseDsmPenalty * 100.0) / 100.0);
        result.put("valueAtRisk", Math.round(valueAtRisk * 100.0) / 100.0);
        result.put("constraintBreaches", constraintBreaches);
        result.put("isSafe", constraintBreaches == 0);

        return result;
    }
}
