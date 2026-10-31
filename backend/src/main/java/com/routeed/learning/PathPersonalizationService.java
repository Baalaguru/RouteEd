package com.routeed.learning;

import com.routeed.assessment.LearnerAssessment;
import com.routeed.course.*;
import com.routeed.user.User;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.routeed.communication.EmailService;

@Service
public class PathPersonalizationService {
    private final CourseRepository courses; private final CourseModuleRepository modules; private final LearnerPathRepository paths; private final OllamaLlmService ollama; private final EmailService emails;
    public PathPersonalizationService(CourseRepository courses, CourseModuleRepository modules, LearnerPathRepository paths, OllamaLlmService ollama, EmailService emails) { this.courses = courses; this.modules = modules; this.paths = paths; this.ollama = ollama; this.emails = emails; }
    @Transactional
    public LearnerPath generate(LearnerAssessment assessment) {
        Course course = courses.findById(assessment.getAssessment().getCourseId()).orElseThrow(); List<CourseModule> items = modules.findByCourse_IdOrderByDefaultOrder(course.getId());
        List<UUID> defaults = items.stream().map(CourseModule::getId).toList(); List<UUID> order = defaults; List<UUID> skipped = List.of(); Map<String, String> rationale = new LinkedHashMap<>();
        try { OllamaLlmService.PersonalizedRoute ai = ollama.generate(items, assessment.getSkillScoresJson()); Set<UUID> allowed = new HashSet<>(defaults); if (ai.moduleOrder().size() == defaults.size() && new HashSet<>(ai.moduleOrder()).equals(allowed)) order = ai.moduleOrder(); skipped = ai.skippedModuleIds().stream().filter(allowed::contains).distinct().toList(); rationale.putAll(ai.aiRationale()); }
        catch (RuntimeException exception) { rationale.put("fallback", "Your path follows the course sequence while personalization is temporarily unavailable."); }
        LearnerPath saved = paths.save(new LearnerPath(assessment.getLearner(), course, order, skipped, rationale));
        emails.sendWelcomeEmail(assessment.getLearner(), saved);
        return saved;
    }
    @Transactional(readOnly = true)
    public LearnerPath latest(User learner, UUID courseId) { return paths.findFirstByLearner_IdAndCourse_IdOrderByGeneratedAtDesc(learner.getId(), courseId).orElseThrow(() -> new IllegalArgumentException("No personalized path exists for this course")); }
}
