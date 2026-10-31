package com.routeed.assessment;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, UUID> {
    List<Question> findByAssessment_IdOrderByPosition(UUID assessmentId);
}
