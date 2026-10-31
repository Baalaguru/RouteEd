package com.routeed.assessment;

import java.util.UUID;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LearnerAssessmentRepository extends JpaRepository<LearnerAssessment, UUID> {
    Optional<LearnerAssessment> findFirstByLearner_IdOrderByCompletedAtDesc(UUID learnerId);
}
