package com.aipower.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class BidSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    private String originalDraftId;
    
    @Column(columnDefinition = "TEXT")
    private String payloadData; // Final successfully approved bids to market
    
    private LocalDateTime approvedAt = LocalDateTime.now();
    private String approvedByRole = "Manager";
}
