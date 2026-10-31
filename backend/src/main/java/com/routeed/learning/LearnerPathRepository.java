package com.routeed.learning;

import java.util.Optional;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LearnerPathRepository extends JpaRepository<LearnerPath, UUID> {
    Optional<LearnerPath> findFirstByLearner_IdAndCourse_IdOrderByGeneratedAtDesc(UUID learnerId, UUID courseId);
    List<LearnerPath> findByCourse_Id(UUID courseId);
}
