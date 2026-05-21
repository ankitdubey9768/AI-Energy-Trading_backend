package com.aipower.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class BidDraft {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    private String status; // Values: PENDING_APPROVAL, APPROVED_TO_MARKET, REJECTED
    
    @Column(columnDefinition = "TEXT")
    private String payloadData; // Stores the JSON Bid configuration safely for Managers to audit
    
    private LocalDateTime createdAt = LocalDateTime.now();
}
