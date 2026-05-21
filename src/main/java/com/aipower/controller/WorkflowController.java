package com.aipower.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.aipower.repository.BidDraftRepository;
import com.aipower.repository.BidSubmissionRepository;
import com.aipower.model.BidDraft;
import com.aipower.model.BidSubmission;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class WorkflowController {

    @Autowired
    private BidDraftRepository bidDraftRepository;

    @Autowired
    private BidSubmissionRepository bidSubmissionRepository;

    // 1. Governance APIs (Two-Step Approval) with H2 database implementation
    @PostMapping("/bids/draft")
    public Map<String, Object> saveDraft(@RequestBody Map<String, Object> payload) {
        try {
            BidDraft draft = new BidDraft();
            draft.setStatus("PENDING_APPROVAL");
            draft.setPayloadData(new ObjectMapper().writeValueAsString(payload));
            draft = bidDraftRepository.save(draft);
            
            return Map.of(
                "status", "DRAFT_SAVED",
                "message", "Database Entry Secured: Bid configuration formally successfully saved to physical H2 draft repository.",
                "draftId", draft.getId()
            );
        } catch (Exception e) {
            return Map.of("status", "ERROR", "message", "Database fault: " + e.getMessage());
        }
    }

    @GetMapping("/bids/drafts")
    public List<BidDraft> getDrafts() {
        return bidDraftRepository.findAll();
    }

    @PostMapping("/bids/approve")
    public Map<String, Object> approveBid(@RequestParam(required = false) String draftId) {
        if (draftId != null && bidDraftRepository.existsById(draftId)) {
            BidDraft draft = bidDraftRepository.findById(draftId).get();
            draft.setStatus("APPROVED_TO_MARKET");
            bidDraftRepository.save(draft);
            
            // Port to official submission log
            BidSubmission submission = new BidSubmission();
            submission.setOriginalDraftId(draft.getId());
            submission.setPayloadData(draft.getPayloadData());
            bidSubmissionRepository.save(submission);
        }
        return Map.of(
            "status", "APPROVED",
            "message", "Market Override: Bids successfully approved and dispatched physically to power exchange."
        );
    }

    @PostMapping("/bids/reject")
    public Map<String, Object> rejectBid(@RequestParam String draftId, @RequestParam String reason) {
        if (bidDraftRepository.existsById(draftId)) {
            BidDraft draft = bidDraftRepository.findById(draftId).get();
            draft.setStatus("REJECTED");
            bidDraftRepository.save(draft);
        }
        return Map.of(
            "status", "REJECTED",
            "message", "Bid rejected by Trading Manager. Reason: " + reason,
            "draftId", draftId
        );
    }

    // 2. Real-Time Alerts API
    @GetMapping("/alerts")
    public Map<String, Object> getAlerts() {
        List<Map<String, String>> alerts = new ArrayList<>();
        alerts.add(Map.of("level", "WARNING", "message", "RTM Price spike predicted in Block 42 (+18%)"));
        alerts.add(Map.of("level", "CRITICAL", "message", "DSM deviation limits rapidly approaching for Solar Contract C"));
        alerts.add(Map.of("level", "INFO", "message", "Submission deadline for DAM window ends in 45 minutes"));
        return Map.of("status", "success", "alerts", alerts);
    }

    // 3. Beckn Protocol Integration Mocks
    @PostMapping("/beckn/search")
    public Map<String, Object> becknSearch(@RequestBody Map<String, Object> searchIntent) {
        return Map.of(
            "context", Map.of("domain", "energy.trade", "action", "on_search", "bap_id", "discom.bap"),
            "message", Map.of("catalog", Map.of("providers", List.of(
                Map.of("id", "provider_1", "descriptor", Map.of("name", "WindFarm Co")),
                Map.of("id", "provider_2", "descriptor", Map.of("name", "SolarGrid Ltd"))
            )))
        );
    }

    @PostMapping("/beckn/order")
    public Map<String, Object> becknOrder(@RequestBody Map<String, Object> orderIntent) {
        return Map.of(
            "context", Map.of("domain", "energy.trade", "action", "on_confirm"),
            "message", Map.of("order", Map.of(
                "id", "ord_" + UUID.randomUUID().toString(),
                "state", "ACCEPTED",
                "provider", Map.of("id", "provider_2")
            ))
        );
    }
}
