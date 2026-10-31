package com.routeed.learning;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ProgressEventRepository extends JpaRepository<ProgressEvent, UUID> {
    Optional<ProgressEvent> findByLearner_IdAndModule_Id(UUID learnerId, UUID moduleId);
    List<ProgressEvent> findByLearner_IdAndModule_Course_Id(UUID learnerId, UUID courseId);
}
