package com.aipower.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.aipower.model.BidSubmission;

@Repository
public interface BidSubmissionRepository extends JpaRepository<BidSubmission, String> {
}
